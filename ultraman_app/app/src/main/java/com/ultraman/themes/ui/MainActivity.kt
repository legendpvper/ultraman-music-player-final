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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Menu
import android.view.MenuItem
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
import com.ultraman.themes.model.UltramanCharacter
import com.ultraman.themes.model.UltramanSong
import com.ultraman.themes.service.MusicService

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var musicService: MusicService? = null
    private var bound = false
    private val characters = SongRepository.characters
    private lateinit var adapter: CharacterAdapter

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
            musicService = (binder as MusicService.MusicBinder).getService()
            bound = true
            refreshMiniPlayer()
        }
        override fun onServiceDisconnected(name: ComponentName) { bound = false }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Ultraman Music Player"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS), 100)
        }

        adapter = CharacterAdapter(characters) { character ->
            startActivity(Intent(this, SongListActivity::class.java)
                .putExtra(SongListActivity.EXTRA_SERIES, character.series))
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
            runOnUiThread { updateMiniPlayer(song, playing) }
        }
    }

    override fun onPause() {
        super.onPause()
        MusicService.onStateChanged = null
    }
	
	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.main_menu, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return when (item.itemId) {
			R.id.action_local_imports -> {
				startActivity(Intent(this, LocalImportsActivity::class.java))
				true
			}
			else -> super.onOptionsItemSelected(item)
		}
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

class CharacterAdapter(
    private val characters: List<UltramanCharacter>,
    private val onClick: (UltramanCharacter) -> Unit
) : RecyclerView.Adapter<CharacterAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val card: CardView   = view.findViewById(R.id.songCard)
        val number: TextView = view.findViewById(R.id.songNumber)
        val title: TextView  = view.findViewById(R.id.songTitle)
        val artist: TextView = view.findViewById(R.id.songArtist)
        val series: TextView = view.findViewById(R.id.songSeries)
        val year: TextView   = view.findViewById(R.id.songYear)
        val strip: View      = view.findViewById(R.id.colorStrip)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_song, parent, false))

    override fun getItemCount() = characters.size

    override fun onBindViewHolder(h: VH, pos: Int) {
        val character = characters[pos]
        val isCurrentSeries = MusicService.currentSong?.series == character.series
        h.number.text = "%02d".format(pos + 1)
        h.title.text  = character.name
        h.artist.text = "${character.songs.size} song${if (character.songs.size > 1) "s" else ""}"
        h.series.text = character.series
        h.year.text   = character.year.toString()
        try { h.strip.setBackgroundColor(Color.parseColor(character.color)) } catch (_: Exception) {}
        h.card.cardElevation = if (isCurrentSeries) 12f else 4f
        h.title.setTextColor(
            if (isCurrentSeries) ContextCompat.getColor(h.itemView.context, R.color.ultraman_blue)
            else ContextCompat.getColor(h.itemView.context, android.R.color.black))
        h.card.setOnClickListener { onClick(character) }
    }
}