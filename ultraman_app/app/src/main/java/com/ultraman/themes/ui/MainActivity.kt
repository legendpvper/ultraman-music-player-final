package com.ultraman.themes.ui

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ultraman.themes.R
import com.ultraman.themes.databinding.ActivityMainBinding
import com.ultraman.themes.model.SongRepository
import com.ultraman.themes.model.UltramanSong
import com.ultraman.themes.service.MusicService

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var musicService: MusicService? = null
    private var bound = false
    private val songs = SongRepository.songs
    private lateinit var adapter: SongAdapter

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
            musicService = (binder as MusicService.MusicBinder).getService()
            bound = true
            refreshMiniPlayer()
        }
        override fun onServiceDisconnected(name: ComponentName) {
            bound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Ultraman Opening Themes"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS), 100)
        }

        adapter = SongAdapter(songs) { song ->
            val idx = songs.indexOf(song)
            MusicService.currentIndex = idx
            startMusicService(song.id)
            startActivity(Intent(this, PlayerActivity::class.java)
                .putExtra(MusicService.EXTRA_SONG_ID, song.id))
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

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
    }

    private fun startMusicService(songId: Int) {
        val intent = Intent(this, MusicService::class.java).apply {
            action = MusicService.ACTION_PLAY
            putExtra(MusicService.EXTRA_SONG_ID, songId)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService(intent) else startService(intent)
    }

    override fun onStart() {
        super.onStart()
        bindService(Intent(this, MusicService::class.java), connection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        if (bound) { unbindService(connection); bound = false }
    }

    override fun onResume() {
        super.onResume()
        refreshMiniPlayer()
        MusicService.onStateChanged = { song, playing ->
            runOnUiThread {
                updateMiniPlayer(song, playing)
                adapter.notifyDataSetChanged()
            }
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

class SongAdapter(
    private val songs: List<UltramanSong>,
    private val onClick: (UltramanSong) -> Unit
) : RecyclerView.Adapter<SongAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val card: CardView     = view.findViewById(R.id.songCard)
        val number: TextView   = view.findViewById(R.id.songNumber)
        val title: TextView    = view.findViewById(R.id.songTitle)
        val artist: TextView   = view.findViewById(R.id.songArtist)
        val series: TextView   = view.findViewById(R.id.songSeries)
        val year: TextView     = view.findViewById(R.id.songYear)
        val strip: View        = view.findViewById(R.id.colorStrip)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_song, parent, false))

    override fun getItemCount() = songs.size

    override fun onBindViewHolder(h: VH, pos: Int) {
        val song = songs[pos]
        val isCurrent = MusicService.currentSong?.id == song.id
        h.number.text  = "%02d".format(pos + 1)
        h.title.text   = song.title
        h.artist.text  = song.artist
        h.series.text  = song.series
        h.year.text    = song.year.toString()
        try { h.strip.setBackgroundColor(Color.parseColor(song.color)) } catch (_: Exception) {}
        h.card.cardElevation = if (isCurrent) 12f else 4f
        h.title.setTextColor(
            if (isCurrent) ContextCompat.getColor(h.itemView.context, R.color.ultraman_blue)
            else ContextCompat.getColor(h.itemView.context, android.R.color.black))
        h.card.setOnClickListener { onClick(song) }
    }
}
