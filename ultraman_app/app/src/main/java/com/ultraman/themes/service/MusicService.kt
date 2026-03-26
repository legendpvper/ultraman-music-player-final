package com.ultraman.themes.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import com.ultraman.themes.R
import com.ultraman.themes.model.SongRepository
import com.ultraman.themes.model.UltramanSong
import com.ultraman.themes.ui.PlayerActivity

class MusicService : Service() {

    companion object {
        const val CHANNEL_ID     = "ultraman_music_channel"
        const val NOTIFICATION_ID = 101
        const val ACTION_PLAY    = "ACTION_PLAY"
        const val ACTION_PAUSE   = "ACTION_PAUSE"
        const val ACTION_NEXT    = "ACTION_NEXT"
        const val ACTION_PREV    = "ACTION_PREV"
        const val ACTION_STOP    = "ACTION_STOP"
        const val EXTRA_SONG_ID  = "EXTRA_SONG_ID"

        // Shared state for UI
        var currentSong: UltramanSong? = null
        var isPlaying: Boolean = false
        var currentIndex: Int = 0
        var onStateChanged: ((UltramanSong, Boolean) -> Unit)? = null
        var onProgressUpdate: ((Int, Int) -> Unit)? = null  // current ms, total ms
		// Queue
		val queue: ArrayDeque<UltramanSong> = ArrayDeque()
		var onQueueChanged: (() -> Unit)? = null

		fun addToQueue(song: UltramanSong) {
			queue.add(song)
			onQueueChanged?.invoke()
		}

		fun removeFromQueue(index: Int) {
			if (index in queue.indices) {
				queue.removeAt(index)
				onQueueChanged?.invoke()
			}
		}
		
		fun addLocalSongToQueue(song: com.ultraman.themes.model.LocalSong) {
			// We store local songs in queue as a special UltramanSong with id = -1
			val tempSong = UltramanSong(
				id            = -1,
				title         = song.title,
				artist        = song.artist,
				series        = "Local Imports",
				year          = 0,
				assetFileName = song.uri,  // store URI here
				color         = "#455A64"
			)
			queue.add(tempSong)
			onQueueChanged?.invoke()
		}
		
    }

    inner class MusicBinder : Binder() {
        fun getService() = this@MusicService
    }

    private val binder = MusicBinder()
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var mediaSession: MediaSessionCompat
    private val songs = SongRepository.songs
    private var progressRunnable: Runnable? = null
    private val handler = android.os.Handler(android.os.Looper.getMainLooper())

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        setupMediaSession()
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY  -> {
                val id = intent.getIntExtra(EXTRA_SONG_ID, -1)
                if (id != -1) {
                    val song = songs.find { it.id == id }
                    if (song != null) {
                        currentIndex = songs.indexOf(song)
                        playSong(song)
                    }
                } else {
                    resumePlayback()
                }
            }
            ACTION_PAUSE -> pausePlayback()
            ACTION_NEXT  -> playNext()
            ACTION_PREV  -> playPrev()
            ACTION_STOP  -> { stopPlayback(); stopSelf() }
        }
        return START_STICKY
    }

    fun playSong(song: UltramanSong) {
        stopMediaPlayer()
        currentSong = song
        isPlaying = false

        try {
            val afd = assets.openFd(song.assetFileName)
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                afd.close()
                prepare()
                start()
                setOnCompletionListener { playNext() }
            }
            isPlaying = true
            onStateChanged?.invoke(song, true)
            startForeground(NOTIFICATION_ID, buildNotification(song, true))
            startProgressUpdates()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun resumePlayback() {
        mediaPlayer?.let { mp ->
            if (!mp.isPlaying) {
                mp.start()
                isPlaying = true
                currentSong?.let {
                    onStateChanged?.invoke(it, true)
                    updateNotification(it, true)
                    startProgressUpdates()
                }
            }
        }
    }

    fun pausePlayback() {
        mediaPlayer?.let { mp ->
            if (mp.isPlaying) {
                mp.pause()
                isPlaying = false
                stopProgressUpdates()
                currentSong?.let {
                    onStateChanged?.invoke(it, false)
                    updateNotification(it, false)
                }
            }
        }
    }

	fun playNext() {
		if (MusicService.queue.isNotEmpty()) {
			val nextSong = MusicService.queue.removeFirst()
			MusicService.onQueueChanged?.invoke()
			if (nextSong.id == -1) {
				// It's a local song, play via URI
				val localSong = com.ultraman.themes.model.LocalSong(
					id     = nextSong.assetFileName,
					title  = nextSong.title,
					artist = nextSong.artist,
					uri    = nextSong.assetFileName
				)
				playLocalSong(localSong)
			} else {
				MusicService.currentIndex = songs.indexOf(nextSong)
				playSong(nextSong)
			}
		} else {
			currentIndex = (currentIndex + 1) % songs.size
			playSong(songs[currentIndex])
		}
	}

    fun playPrev() {
        // If more than 3 seconds in, restart current song; otherwise go to previous
        val position = mediaPlayer?.currentPosition ?: 0
        if (position > 3000) {
            mediaPlayer?.seekTo(0)
        } else {
            currentIndex = if (currentIndex - 1 < 0) songs.size - 1 else currentIndex - 1
            playSong(songs[currentIndex])
        }
    }

    fun stopPlayback() {
        stopMediaPlayer()
        stopProgressUpdates()
        currentSong = null
        isPlaying = false
    }

    fun seekTo(ms: Int) {
        mediaPlayer?.seekTo(ms)
    }
	
	fun playLocalSong(song: com.ultraman.themes.model.LocalSong) {
		stopMediaPlayer()
		// Create a temporary UltramanSong to reuse existing UI/state
		val tempSong = UltramanSong(
			id            = -1,
			title         = song.title,
			artist        = song.artist,
			series        = "Local Imports",
			year          = 0,
			assetFileName = "",
			color         = "#455A64"
		)
		currentSong = tempSong
		isPlaying = false
		try {
			val uri = android.net.Uri.parse(song.uri)
			mediaPlayer = MediaPlayer().apply {
				setAudioAttributes(
					android.media.AudioAttributes.Builder()
						.setUsage(android.media.AudioAttributes.USAGE_MEDIA)
						.setContentType(android.media.AudioAttributes.CONTENT_TYPE_MUSIC)
						.build()
				)
				setDataSource(applicationContext, uri)
				prepare()
				start()
				setOnCompletionListener {
					if (MusicService.queue.isNotEmpty()) {
						playNext()
					} else {
						MusicService.isPlaying = false
						MusicService.onStateChanged?.invoke(tempSong, false)
					}
				}
			}
			isPlaying = true
			onStateChanged?.invoke(tempSong, true)
			startForeground(NOTIFICATION_ID, buildNotification(tempSong, true))
			startProgressUpdates()
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}

    fun getCurrentPosition(): Int = mediaPlayer?.currentPosition ?: 0
    fun getDuration(): Int = mediaPlayer?.duration ?: 0

    private fun stopMediaPlayer() {
        mediaPlayer?.apply { if (isPlaying) stop(); release() }
        mediaPlayer = null
    }

    private fun startProgressUpdates() {
        stopProgressUpdates()
        progressRunnable = object : Runnable {
            override fun run() {
                mediaPlayer?.let { mp ->
                    if (mp.isPlaying) {
                        onProgressUpdate?.invoke(mp.currentPosition, mp.duration)
                    }
                }
                handler.postDelayed(this, 500)
            }
        }
        handler.post(progressRunnable!!)
    }

    private fun stopProgressUpdates() {
        progressRunnable?.let { handler.removeCallbacks(it) }
        progressRunnable = null
    }

    private fun setupMediaSession() {
        mediaSession = MediaSessionCompat(this, "UltramanSession").apply {
            isActive = true
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "Ultraman Music Player",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                setShowBadge(false)
                description = "Ultraman opening themes"
            }
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
        }
    }

    private fun buildNotification(song: UltramanSong, playing: Boolean): Notification {
        val openIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, PlayerActivity::class.java).apply {
                putExtra(EXTRA_SONG_ID, song.id)
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        fun serviceIntent(action: String, reqCode: Int) = PendingIntent.getService(
            this, reqCode,
            Intent(this, MusicService::class.java).setAction(action),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val playPauseIcon = if (playing) R.drawable.ic_pause else R.drawable.ic_play
        val playPauseAction = if (playing) ACTION_PAUSE else ACTION_PLAY

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(song.title)
            .setContentText("${song.series} (${song.year}) • ${song.artist}")
            .setSmallIcon(R.drawable.ic_ultraman_notification)
            .setContentIntent(openIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .setOngoing(playing)
            .addAction(R.drawable.ic_prev, "Prev", serviceIntent(ACTION_PREV, 1))
            .addAction(playPauseIcon, if (playing) "Pause" else "Play", serviceIntent(playPauseAction, 2))
            .addAction(R.drawable.ic_next, "Next", serviceIntent(ACTION_NEXT, 3))
            .addAction(R.drawable.ic_stop, "Stop", serviceIntent(ACTION_STOP, 4))
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
                    .setShowActionsInCompactView(0, 1, 2)
            )
            .setDeleteIntent(serviceIntent(ACTION_STOP, 5))
            .build()
    }

    private fun updateNotification(song: UltramanSong, playing: Boolean) {
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .notify(NOTIFICATION_ID, buildNotification(song, playing))
    }

    override fun onDestroy() {
        stopPlayback()
        mediaSession.release()
        super.onDestroy()
    }
}
