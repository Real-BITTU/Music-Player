package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.PlaylistRepository
import com.example.data.local.MusicDatabase
import com.example.data.local.MusicLocalRepository
import com.example.model.FullPlayerTab
import com.example.model.Song
import com.example.player.MusicPlayerManager
import com.example.player.PlaybackState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MusicViewModel(application: Application) : AndroidViewModel(application) {

  private val database = MusicDatabase.getDatabase(application)
  private val localRepository = MusicLocalRepository(database.musicDao())

  private val playerManager = MusicPlayerManager(
    context = application.applicationContext,
    coroutineScope = viewModelScope
  )

  val playlist: List<Song> = PlaylistRepository.defaultPlaylist
  val genreFilters: List<String> = listOf("All", "Favorites") + PlaylistRepository.genreFilters.filter { it != "All" }

  val playbackState: StateFlow<PlaybackState> = playerManager.playbackState

  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _selectedGenre = MutableStateFlow("All")
  val selectedGenre: StateFlow<String> = _selectedGenre.asStateFlow()

  // Room Local Storage: Favorites
  val favorites: StateFlow<Set<String>> = localRepository.favoriteSongIds
    .map { it.toSet() }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.Eagerly,
      initialValue = setOf("song_1", "song_3")
    )

  // Room Local Storage: Recently Played Songs
  val recentlyPlayedSongs: StateFlow<List<Song>> = localRepository.recentHistory
    .map { historyList ->
      if (historyList.isEmpty()) {
        playlist
      } else {
        val uniqueIds = historyList.map { it.songId }.distinct()
        val mapped = uniqueIds.mapNotNull { id -> playlist.find { it.id == id } }
        if (mapped.isNotEmpty()) mapped else playlist
      }
    }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.Lazily,
      initialValue = playlist
    )

  private val _isPlayerExpanded = MutableStateFlow(false)
  val isPlayerExpanded: StateFlow<Boolean> = _isPlayerExpanded.asStateFlow()

  private val _activePlayerTab = MutableStateFlow(FullPlayerTab.NOW_PLAYING)
  val activePlayerTab: StateFlow<FullPlayerTab> = _activePlayerTab.asStateFlow()

  // Sleep Timer
  private val _sleepTimerMinutes = MutableStateFlow<Int?>(null)
  val sleepTimerMinutes: StateFlow<Int?> = _sleepTimerMinutes.asStateFlow()
  private var sleepTimerJob: Job? = null

  val currentSong: StateFlow<Song?> = playbackState.combine(_searchQuery) { state, _ ->
    if (state.currentSongIndex in playlist.indices) {
      playlist[state.currentSongIndex]
    } else {
      playlist.firstOrNull()
    }
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.Eagerly,
    initialValue = playlist.firstOrNull()
  )

  // Filter songs by title or artist and selected genre/favorites
  val filteredSongs: StateFlow<List<Song>> = combine(
    _searchQuery,
    _selectedGenre,
    favorites
  ) { query, genre, favSet ->
    playlist.filter { song ->
      val matchesGenre = when (genre) {
        "All" -> true
        "Favorites" -> favSet.contains(song.id)
        else -> song.genre.equals(genre, ignoreCase = true)
      }
      val matchesQuery = query.isBlank() ||
        song.title.contains(query, ignoreCase = true) ||
        song.artist.contains(query, ignoreCase = true) ||
        song.album.contains(query, ignoreCase = true)
      matchesGenre && matchesQuery
    }
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.Lazily,
    initialValue = playlist
  )

  init {
    viewModelScope.launch {
      localRepository.seedInitialFavoritesIfEmpty(setOf("song_1", "song_3"))

      // Restore last playback state from Room if available
      val lastPlayback = localRepository.lastPlayback.firstOrNull()
      val startIdx = if (lastPlayback != null) {
        val idx = playlist.indexOfFirst { it.id == lastPlayback.lastSongId }
        if (idx != -1) idx else 0
      } else 0

      playerManager.setPlaylist(playlist, startIndex = startIdx, playWhenReady = false)
      if (lastPlayback != null && lastPlayback.lastPositionMs > 0) {
        playerManager.seekTo(lastPlayback.lastPositionMs)
      }
    }
  }

  fun playSong(song: Song) {
    val index = playlist.indexOfFirst { it.id == song.id }
    if (index != -1) {
      playerManager.playSongAtIndex(index)
      recordPlayHistory(song.id)
    }
  }

  fun playSongAtIndex(index: Int) {
    if (index in playlist.indices) {
      playerManager.playSongAtIndex(index)
      recordPlayHistory(playlist[index].id)
    }
  }

  private fun recordPlayHistory(songId: String) {
    viewModelScope.launch {
      localRepository.recordSongPlayed(songId)
      localRepository.saveLastPlayback(songId, 0L)
    }
  }

  fun togglePlayPause() {
    playerManager.togglePlayPause()
    currentSong.value?.let { song ->
      viewModelScope.launch {
        localRepository.saveLastPlayback(song.id, playbackState.value.currentPositionMs)
      }
    }
  }

  fun playNext() {
    playerManager.playNext()
  }

  fun playPrevious() {
    playerManager.playPrevious()
  }

  fun seekTo(positionMs: Long) {
    playerManager.seekTo(positionMs)
    currentSong.value?.let { song ->
      viewModelScope.launch {
        localRepository.saveLastPlayback(song.id, positionMs)
      }
    }
  }

  fun toggleShuffle() {
    playerManager.toggleShuffle()
  }

  fun cycleRepeatMode() {
    playerManager.cycleRepeatMode()
  }

  fun toggleFavorite(songId: String) {
    val isCurrentlyFav = favorites.value.contains(songId)
    viewModelScope.launch {
      localRepository.toggleFavorite(songId, isCurrentlyFav)
    }
  }

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun selectGenre(genre: String) {
    _selectedGenre.value = genre
  }

  fun setPlayerExpanded(expanded: Boolean) {
    _isPlayerExpanded.value = expanded
    if (expanded) {
      _activePlayerTab.value = FullPlayerTab.NOW_PLAYING
    }
  }

  fun setActivePlayerTab(tab: FullPlayerTab) {
    _activePlayerTab.value = tab
  }

  fun setPlaybackSpeed(speed: Float) {
    playerManager.setPlaybackSpeed(speed)
  }

  fun setVolume(volume: Float) {
    playerManager.setVolume(volume)
  }

  fun setSleepTimer(minutes: Int?) {
    sleepTimerJob?.cancel()
    _sleepTimerMinutes.value = minutes
    if (minutes != null && minutes > 0) {
      sleepTimerJob = viewModelScope.launch {
        var remaining = minutes
        while (remaining > 0) {
          delay(60_000L) // 1 minute
          remaining--
          _sleepTimerMinutes.value = remaining
        }
        playerManager.pause()
        _sleepTimerMinutes.value = null
      }
    }
  }

  fun playAll() {
    val currentFiltered = filteredSongs.value
    if (currentFiltered.isNotEmpty()) {
      playSong(currentFiltered.first())
    } else if (playlist.isNotEmpty()) {
      playSong(playlist.first())
    }
  }

  fun shuffleAll() {
    if (!playbackState.value.isShuffleEnabled) {
      playerManager.toggleShuffle()
    }
    val targetList = filteredSongs.value.ifEmpty { playlist }
    val randomSong = targetList.randomOrNull()
    if (randomSong != null) {
      playSong(randomSong)
    }
  }

  fun clearError() {
    playerManager.clearError()
  }

  override fun onCleared() {
    super.onCleared()
    sleepTimerJob?.cancel()
    playerManager.release()
  }
}
