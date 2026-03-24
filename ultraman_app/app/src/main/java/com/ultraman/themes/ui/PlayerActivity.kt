package com.ultraman.themes.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.ultraman.themes.R
import com.ultraman.themes.databinding.ActivityPlayerBinding
import com.ultraman.themes.model.SongRepository
import com.ultraman.themes.model.UltramanSong
import com.ultraman.themes.service.MusicService

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private var musicService: MusicService? = null
    private var bound = false
    private val songs = SongRepository.songs
    private var isSeeking = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
            musicService = (binder as MusicService.MusicBinder).getService()
            bound = true
            // Start playing the requested song if not already playing it
            val requestedId = intent.getIntExtra(MusicService.EXTRA_SONG_ID, -1)
            if (requestedId != -1 && MusicService.currentSong?.id != requestedId) {
                val song = songs.find { it.id == requestedId }
                if (song != null) {
                    MusicService.currentIndex = songs.indexOf(song)
                    musicService?.playSong(song)
                }
            }
            updateUI(MusicService.currentSong, MusicService.isPlaying)
        }
        override fun onServiceDisconnected(name: ComponentName) { bound = false }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.playerToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.playerToolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        binding.playPauseButton.setOnClickListener {
            if (MusicService.isPlaying) musicService?.pausePlayback()
            else musicService?.resumePlayback()
        }
        binding.nextButton.setOnClickListener { musicService?.playNext() }
        binding.prevButton.setOnClickListener { musicService?.playPrev() }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(sb: SeekBar?) { isSeeking = true }
            override fun onStopTrackingTouch(sb: SeekBar?) {
                sb?.let { musicService?.seekTo(it.progress) }
                isSeeking = false
            }
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) binding.currentTimeText.text = formatTime(progress)
            }
        })
    }

    override fun onStart() {
        super.onStart()
        // Start service if needed
        val intent = Intent(this, MusicService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService(intent) else startService(intent)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        if (bound) { unbindService(connection); bound = false }
    }

    override fun onResume() {
        super.onResume()
        MusicService.onStateChanged = { song, playing ->
            runOnUiThread { updateUI(song, playing) }
        }
        MusicService.onProgressUpdate = { current, total ->
            runOnUiThread {
                if (!isSeeking && total > 0) {
                    binding.seekBar.max = total
                    binding.seekBar.progress = current
                    binding.currentTimeText.text = formatTime(current)
                    binding.durationText.text = formatTime(total)
                }
            }
        }
        // Refresh UI from current state
        updateUI(MusicService.currentSong, MusicService.isPlaying)
    }

    override fun onPause() {
        super.onPause()
        MusicService.onStateChanged = null
        MusicService.onProgressUpdate = null
    }

    private fun updateUI(song: UltramanSong?, playing: Boolean) {
        song ?: return
        val idx = songs.indexOf(song)
        binding.trackCounterText.text = "${idx + 1} / ${songs.size}"
        binding.songTitleText.text    = song.title
        binding.songSeriesText.text   = "${song.series} (${song.year})"
        binding.songArtistText.text   = song.artist
        binding.playPauseButton.setImageResource(
            if (playing) R.drawable.ic_pause_large else R.drawable.ic_play_large)
        try {
            binding.playerHeader.setBackgroundColor(Color.parseColor(song.color))
        } catch (_: Exception) {}
    }

    private fun formatTime(ms: Int): String {
        val s = ms / 1000
        return "%d:%02d".format(s / 60, s % 60)
    }
}
