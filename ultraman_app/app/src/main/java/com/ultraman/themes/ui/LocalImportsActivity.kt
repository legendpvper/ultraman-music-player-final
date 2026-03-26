package com.ultraman.themes.ui

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ultraman.themes.R
import com.ultraman.themes.databinding.ActivityLocalImportsBinding
import com.ultraman.themes.model.LocalSong
import com.ultraman.themes.model.LocalSongRepository
import com.ultraman.themes.model.UltramanSong
import com.ultraman.themes.service.MusicService

class LocalImportsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLocalImportsBinding
    private lateinit var songs: MutableList<LocalSong>
    private lateinit var adapter: LocalSongAdapter
    private var musicService: MusicService? = null
    private var bound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
            musicService = (binder as MusicService.MusicBinder).getService()
            bound = true
            refreshMiniPlayer()
        }
        override fun onServiceDisconnected(name: ComponentName) { bound = false }
    }

    private val pickAudio = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                // Persist permission so we can access the file later
                contentResolver.takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                importSong(uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocalImportsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Local Imports"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        songs = LocalSongRepository.getSongs(this)

        adapter = LocalSongAdapter(
            songs,
            onPlay = { song -> playSong(song) },
            onAddToQueue = { song ->
                // Convert LocalSong to a playable format for the queue
                MusicService.addLocalSongToQueue(song)
                Toast.makeText(this, "\"${song.title}\" added to queue", Toast.LENGTH_SHORT).show()
            },
            onDelete = { song ->
                AlertDialog.Builder(this)
                    .setTitle("Remove Song")
                    .setMessage("Remove \"${song.title}\" from Local Imports?")
                    .setPositiveButton("Remove") { _, _ ->
                        LocalSongRepository.removeSong(this, song.id)
                        val idx = songs.indexOfFirst { it.id == song.id }
                        if (idx != -1) {
                            songs.removeAt(idx)
                            adapter.notifyItemRemoved(idx)
                            updateEmptyState()
                        }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        )

        binding.localSongsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.localSongsRecyclerView.adapter = adapter

        binding.addSongFab.setOnClickListener { openFilePicker() }

        binding.miniPlayerLayout.setOnClickListener {
            MusicService.currentSong?.let {
                startActivity(Intent(this, PlayerActivity::class.java)
                    .putExtra(MusicService.EXTRA_SONG_ID, it.id))
            }
        }
        binding.miniPlayerPlayPause.setOnClickListener {
            val action = if (MusicService.isPlaying) MusicService.ACTION_PAUSE else MusicService.ACTION_PLAY
            startService(Intent(this, MusicService::class.java).setAction(action))
        }

        updateEmptyState()
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "audio/*"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        }
        pickAudio.launch(intent)
    }

    private fun importSong(uri: Uri) {
        try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(this, uri)
            val title  = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                ?: uri.lastPathSegment?.removeSuffix(".mp3") ?: "Unknown Title"
            val artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
                ?: "Unknown Artist"
            retriever.release()

            val song = LocalSong(
                id     = uri.toString(),
                title  = title,
                artist = artist,
                uri    = uri.toString()
            )
            LocalSongRepository.addSong(this, song)
            songs.clear()
            songs.addAll(LocalSongRepository.getSongs(this))
            adapter.notifyDataSetChanged()
            updateEmptyState()
            Toast.makeText(this, "\"$title\" imported!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to import song", Toast.LENGTH_SHORT).show()
        }
    }

    private fun playSong(song: LocalSong) {
        musicService?.playLocalSong(song) ?: run {
            Toast.makeText(this, "Service not ready, try again", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateEmptyState() {
        binding.emptyText.visibility = if (songs.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onStart() {
        super.onStart()
        bindService(Intent(this, MusicService::class.java), connection, Context.BIND_AUTO_CREATE)
        val intent = Intent(this, MusicService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService(intent) else startService(intent)
    }

    override fun onStop() {
        super.onStop()
        if (bound) { unbindService(connection); bound = false }
    }

    override fun onResume() {
        super.onResume()
        refreshMiniPlayer()
        MusicService.onStateChanged = { song, playing ->
            runOnUiThread { updateMiniPlayer(song, playing) }
        }
    }

    override fun onPause() {
        super.onPause()
        MusicService.onStateChanged = null
    }

    private fun refreshMiniPlayer() {
        val song = MusicService.currentSong
        if (song != null) updateMiniPlayer(song, MusicService.isPlaying)
        else binding.miniPlayerLayout.visibility = View.GONE
    }

    private fun updateMiniPlayer(song: UltramanSong, playing: Boolean) {
        binding.miniPlayerLayout.visibility = View.VISIBLE
        binding.miniPlayerTitle.text = song.title
        binding.miniPlayerSeries.text = song.series
        binding.miniPlayerPlayPause.setImageResource(
            if (playing) R.drawable.ic_pause else R.drawable.ic_play)
    }
}

class LocalSongAdapter(
    private val songs: MutableList<LocalSong>,
    private val onPlay: (LocalSong) -> Unit,
    private val onAddToQueue: (LocalSong) -> Unit,
    private val onDelete: (LocalSong) -> Unit
) : RecyclerView.Adapter<LocalSongAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val card: CardView        = view.findViewById(R.id.localSongCard)
        val title: TextView       = view.findViewById(R.id.localSongTitle)
        val artist: TextView      = view.findViewById(R.id.localSongArtist)
        val addQueue: ImageButton = view.findViewById(R.id.localAddToQueueButton)
        val delete: ImageButton   = view.findViewById(R.id.localDeleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_local_song, parent, false))

    override fun getItemCount() = songs.size

    override fun onBindViewHolder(h: VH, pos: Int) {
        val song = songs[pos]
        h.title.text  = song.title
        h.artist.text = song.artist
        h.card.setOnClickListener { onPlay(song) }
        h.addQueue.setOnClickListener { onAddToQueue(song) }
        h.delete.setOnClickListener { onDelete(song) }
    }
}