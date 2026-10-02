package com.example.player

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import com.example.model.RepeatMode
import com.example.model.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class PlaybackState(
  val currentSongIndex: Int = 0,
  val isPlaying: Boolean = false,
  val isBuffering: Boolean = false,
  val currentPositionMs: Long = 0L,
  val durationMs: Long = 0L,
  val isShuffleEnabled: Boolean = false,
  val repeatMode: RepeatMode = RepeatMode.OFF,
  val playbackSpeed: Float = 1.0f,
  val volume: Float = 1.0f,
  val errorMessage: String? = null
)

class MusicPlayerManager(
  private val context: Context,
  private val coroutineScope: CoroutineScope
) {

  private var exoPlayer: ExoPlayer? = null
  private var songs: List<Song> = emptyList()

  private val _playbackState = MutableStateFlow(PlaybackState())
  val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

  private var progressTrackerJob: Job? = null

  init {
    initializePlayer()
  }

  private fun initializePlayer() {
    val audioAttributes = AudioAttributes.Builder()
      .setUsage(C.USAGE_MEDIA)
      .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
      .build()

    val renderersFactory = DefaultRenderersFactory(context).apply {
      setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF)
      setEnableDecoderFallback(true)
    }

    val player = ExoPlayer.Builder(context, renderersFactory)
      .setAudioAttributes(audioAttributes, true)
      .setHandleAudioBecomingNoisy(true)
      .build()
      .apply {
        repeatMode = Player.REPEAT_MODE_OFF
        shuffleModeEnabled = false

        addListener(object : Player.Listener {
          override fun onPlaybackStateChanged(state: Int) {
            val isBuffering = state == Player.STATE_BUFFERING
            val duration = if (duration > 0) duration else _playbackState.value.durationMs
            _playbackState.value = _playbackState.value.copy(
              isBuffering = isBuffering,
              durationMs = duration
            )

            if (state == Player.STATE_READY) {
              _playbackState.value = _playbackState.value.copy(
                errorMessage = null,
                durationMs = if (duration > 0) duration else _playbackState.value.durationMs
              )
            }
          }

          override fun onIsPlayingChanged(isPlaying: Boolean) {
            _playbackState.value = _playbackState.value.copy(isPlaying = isPlaying)
            if (isPlaying) {
              startProgressTracker()
            } else {
              stopProgressTracker()
            }
          }

          override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            val currentIdx = currentMediaItemIndex
            if (currentIdx in songs.indices) {
              val songDuration = songs[currentIdx].durationMs
              _playbackState.value = _playbackState.value.copy(
                currentSongIndex = currentIdx,
                currentPositionMs = 0L,
                durationMs = songDuration,
                errorMessage = null
              )
            }
          }

          override fun onPlayerError(error: PlaybackException) {
            _playbackState.value = _playbackState.value.copy(
              isPlaying = false,
              isBuffering = false,
              errorMessage = "Playback issue: ${error.localizedMessage ?: "Unable to stream audio"}"
            )
          }
        })
      }

    exoPlayer = player
  }

  fun setPlaylist(newSongs: List<Song>, startIndex: Int = 0, playWhenReady: Boolean = false) {
    this.songs = newSongs
    val player = exoPlayer ?: return

    val mediaItems = newSongs.map { song ->
      MediaItem.Builder()
        .setMediaId(song.id)
        .setUri(song.audioUrl)
        .setMediaMetadata(
          MediaMetadata.Builder()
            .setTitle(song.title)
            .setArtist(song.artist)
            .setAlbumTitle(song.album)
            .build()
        )
        .build()
    }

    player.setMediaItems(mediaItems, startIndex, 0L)
    if (playWhenReady) {
      player.prepare()
      player.playWhenReady = true
    }

    val initialDuration = if (startIndex in newSongs.indices) newSongs[startIndex].durationMs else 0L
    _playbackState.value = _playbackState.value.copy(
      currentSongIndex = startIndex,
      currentPositionMs = 0L,
      durationMs = initialDuration,
      isPlaying = playWhenReady,
      errorMessage = null
    )
  }

  fun playSongAtIndex(index: Int) {
    if (index !in songs.indices) return
    val player = exoPlayer ?: return

    if (player.playbackState == Player.STATE_IDLE) {
      player.prepare()
    }
    if (player.currentMediaItemIndex != index) {
      player.seekTo(index, 0L)
    }
    player.playWhenReady = true
    player.play()

    _playbackState.value = _playbackState.value.copy(
      currentSongIndex = index,
      currentPositionMs = 0L,
      durationMs = songs[index].durationMs,
      isPlaying = true,
      errorMessage = null
    )
  }

  fun togglePlayPause() {
    val player = exoPlayer ?: return
    if (player.isPlaying) {
      player.pause()
    } else {
      if (player.playbackState == Player.STATE_IDLE) {
        player.prepare()
      }
      player.playWhenReady = true
      player.play()
    }
  }

  fun play() {
    val player = exoPlayer ?: return
    if (player.playbackState == Player.STATE_IDLE) {
      player.prepare()
    }
    player.playWhenReady = true
    player.play()
  }

  fun pause() {
    exoPlayer?.pause()
  }

  fun playNext() {
    val player = exoPlayer ?: return
    if (player.playbackState == Player.STATE_IDLE) {
      player.prepare()
    }
    if (player.hasNextMediaItem()) {
      player.seekToNextMediaItem()
      player.playWhenReady = true
      player.play()
    } else if (songs.isNotEmpty()) {
      player.seekTo(0, 0L)
      player.playWhenReady = true
      player.play()
    }
  }

  fun playPrevious() {
    val player = exoPlayer ?: return
    if (player.playbackState == Player.STATE_IDLE) {
      player.prepare()
    }
    if (player.currentPosition > 3000L) {
      player.seekTo(0L)
    } else if (player.hasPreviousMediaItem()) {
      player.seekToPreviousMediaItem()
      player.playWhenReady = true
      player.play()
    } else if (songs.isNotEmpty()) {
      player.seekTo(songs.lastIndex, 0L)
      player.playWhenReady = true
      player.play()
    }
  }

  fun seekTo(positionMs: Long) {
    val player = exoPlayer ?: return
    val validPos = positionMs.coerceIn(0L, player.duration.coerceAtLeast(1000L))
    player.seekTo(validPos)
    _playbackState.value = _playbackState.value.copy(currentPositionMs = validPos)
  }

  fun toggleShuffle() {
    val player = exoPlayer ?: return
    val newShuffle = !_playbackState.value.isShuffleEnabled
    player.shuffleModeEnabled = newShuffle
    _playbackState.value = _playbackState.value.copy(isShuffleEnabled = newShuffle)
  }

  fun cycleRepeatMode() {
    val player = exoPlayer ?: return
    val nextMode = when (_playbackState.value.repeatMode) {
      RepeatMode.OFF -> RepeatMode.ALL
      RepeatMode.ALL -> RepeatMode.ONE
      RepeatMode.ONE -> RepeatMode.OFF
    }

    player.repeatMode = when (nextMode) {
      RepeatMode.OFF -> Player.REPEAT_MODE_OFF
      RepeatMode.ALL -> Player.REPEAT_MODE_ALL
      RepeatMode.ONE -> Player.REPEAT_MODE_ONE
    }

    _playbackState.value = _playbackState.value.copy(repeatMode = nextMode)
  }

  fun setPlaybackSpeed(speed: Float) {
    val player = exoPlayer ?: return
    player.setPlaybackSpeed(speed)
    _playbackState.value = _playbackState.value.copy(playbackSpeed = speed)
  }

  fun setVolume(volume: Float) {
    val player = exoPlayer ?: return
    val clamped = volume.coerceIn(0f, 1f)
    player.volume = clamped
    _playbackState.value = _playbackState.value.copy(volume = clamped)
  }

  private fun startProgressTracker() {
    progressTrackerJob?.cancel()
    progressTrackerJob = coroutineScope.launch(Dispatchers.Main) {
      while (isActive) {
        val player = exoPlayer
        if (player != null && player.isPlaying) {
          val currentPos = player.currentPosition
          val actualDuration = if (player.duration > 0) player.duration else _playbackState.value.durationMs
          _playbackState.value = _playbackState.value.copy(
            currentPositionMs = currentPos,
            durationMs = actualDuration
          )
        }
        delay(250L)
      }
    }
  }

  private fun stopProgressTracker() {
    progressTrackerJob?.cancel()
    progressTrackerJob = null
  }

  fun clearError() {
    _playbackState.value = _playbackState.value.copy(errorMessage = null)
  }

  fun release() {
    stopProgressTracker()
    exoPlayer?.release()
    exoPlayer = null
  }
}
