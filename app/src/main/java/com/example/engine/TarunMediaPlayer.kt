package com.example.engine

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AudioTrack(
    val id: String,
    val title: String,
    val artist: String,
    val uriString: String,
    val durationSeconds: Int = 180,
    val category: String = "Ambient / Focus"
)

enum class PlaybackState {
    IDLE,
    BUFFERING,
    PLAYING,
    PAUSED,
    STOPPED
}

class TarunMediaPlayer private constructor(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null

    private val _playbackState = MutableStateFlow(PlaybackState.IDLE)
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    private val _currentTrack = MutableStateFlow<AudioTrack?>(null)
    val currentTrack: StateFlow<AudioTrack?> = _currentTrack.asStateFlow()

    private val _currentPositionSeconds = MutableStateFlow(0)
    val currentPositionSeconds: StateFlow<Int> = _currentPositionSeconds.asStateFlow()

    private val _playlist = MutableStateFlow<List<AudioTrack>>(getDefaultPlaylist())
    val playlist: StateFlow<List<AudioTrack>> = _playlist.asStateFlow()

    private var currentTrackIndex = 0

    companion object {
        @Volatile
        private var INSTANCE: TarunMediaPlayer? = null

        fun getInstance(context: Context): TarunMediaPlayer {
            return INSTANCE ?: synchronized(this) {
                val instance = TarunMediaPlayer(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }

        fun getDefaultPlaylist(): List<AudioTrack> {
            return listOf(
                AudioTrack(
                    id = "ambient_focus_1",
                    title = "Cybernetic Focus Alpha",
                    artist = "TARUN Soundscapes",
                    uriString = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
                    durationSeconds = 372,
                    category = "Productivity & Focus"
                ),
                AudioTrack(
                    id = "ambient_focus_2",
                    title = "Deep Space Zen Meditation",
                    artist = "TARUN Soundscapes",
                    uriString = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
                    durationSeconds = 423,
                    category = "Meditation & Calm"
                ),
                AudioTrack(
                    id = "ambient_focus_3",
                    title = "Morning Motivation Energizer",
                    artist = "TARUN Soundscapes",
                    uriString = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
                    durationSeconds = 345,
                    category = "Morning Routines"
                )
            )
        }
    }

    fun playTrack(track: AudioTrack) {
        try {
            stop()
            _playbackState.value = PlaybackState.BUFFERING
            _currentTrack.value = track

            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(context, Uri.parse(track.uriString))
                setOnPreparedListener { mp ->
                    mp.start()
                    _playbackState.value = PlaybackState.PLAYING
                }
                setOnCompletionListener {
                    playNext()
                }
                setOnErrorListener { _, _, _ ->
                    _playbackState.value = PlaybackState.STOPPED
                    true
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            _playbackState.value = PlaybackState.STOPPED
        }
    }

    fun playPause() {
        when (_playbackState.value) {
            PlaybackState.PLAYING -> {
                mediaPlayer?.pause()
                _playbackState.value = PlaybackState.PAUSED
            }
            PlaybackState.PAUSED -> {
                mediaPlayer?.start()
                _playbackState.value = PlaybackState.PLAYING
            }
            PlaybackState.STOPPED, PlaybackState.IDLE -> {
                val track = _currentTrack.value ?: _playlist.value.firstOrNull()
                if (track != null) {
                    playTrack(track)
                }
            }
            PlaybackState.BUFFERING -> {}
        }
    }

    fun playNext() {
        val list = _playlist.value
        if (list.isEmpty()) return
        currentTrackIndex = (currentTrackIndex + 1) % list.size
        playTrack(list[currentTrackIndex])
    }

    fun playPrevious() {
        val list = _playlist.value
        if (list.isEmpty()) return
        currentTrackIndex = if (currentTrackIndex - 1 < 0) list.size - 1 else currentTrackIndex - 1
        playTrack(list[currentTrackIndex])
    }

    fun seekTo(seconds: Int) {
        mediaPlayer?.seekTo(seconds * 1000)
    }

    fun stop() {
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.stop()
            }
            mediaPlayer?.release()
            mediaPlayer = null
            _playbackState.value = PlaybackState.STOPPED
        } catch (e: Exception) {
            _playbackState.value = PlaybackState.STOPPED
        }
    }
}
