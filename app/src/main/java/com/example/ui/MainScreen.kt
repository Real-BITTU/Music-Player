package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.Song
import com.example.ui.components.FullScreenPlayerSheet
import com.example.ui.components.HeaderSection
import com.example.ui.components.HeroBanner
import com.example.ui.components.MiniPlayerBar
import com.example.ui.components.RecentlyPlayedSection
import com.example.ui.components.TrackListItem
import com.example.ui.theme.AuraCyan
import com.example.ui.theme.AuraPink
import com.example.ui.theme.AuraViolet
import com.example.ui.theme.DarkBg
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.viewmodel.MusicViewModel

@Composable
fun MainScreen(
  viewModel: MusicViewModel,
  modifier: Modifier = Modifier
) {
  val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()
  val currentSong by viewModel.currentSong.collectAsStateWithLifecycle()
  val filteredSongs by viewModel.filteredSongs.collectAsStateWithLifecycle()
  val playlist = viewModel.playlist
  val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
  val selectedGenre by viewModel.selectedGenre.collectAsStateWithLifecycle()
  val favorites by viewModel.favorites.collectAsStateWithLifecycle()
  val recentlyPlayed by viewModel.recentlyPlayedSongs.collectAsStateWithLifecycle()
  val isPlayerExpanded by viewModel.isPlayerExpanded.collectAsStateWithLifecycle()
  val activePlayerTab by viewModel.activePlayerTab.collectAsStateWithLifecycle()
  val sleepTimerMinutes by viewModel.sleepTimerMinutes.collectAsStateWithLifecycle()

  val snackbarHostState = remember { SnackbarHostState() }

  LaunchedEffect(playbackState.errorMessage) {
    playbackState.errorMessage?.let { msg ->
      snackbarHostState.showSnackbar(msg)
      viewModel.clearError()
    }
  }

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .background(DarkBg),
    snackbarHost = { SnackbarHost(snackbarHostState) },
    containerColor = DarkBg,
    bottomBar = {
      // Persistent Mini Player Bar (shown whenever player sheet is collapsed)
      if (!isPlayerExpanded && currentSong != null) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
        ) {
          MiniPlayerBar(
            currentSong = currentSong,
            isPlaying = playbackState.isPlaying,
            isBuffering = playbackState.isBuffering,
            currentPositionMs = playbackState.currentPositionMs,
            durationMs = playbackState.durationMs,
            isFavorite = favorites.contains(currentSong?.id ?: ""),
            onTogglePlayPause = { viewModel.togglePlayPause() },
            onPlayNext = { viewModel.playNext() },
            onToggleFavorite = { currentSong?.let { viewModel.toggleFavorite(it.id) } },
            onExpandPlayer = { viewModel.setPlayerExpanded(true) }
          )
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .testTag("main_track_list"),
        contentPadding = PaddingValues(bottom = 24.dp)
      ) {
        // Sticky Header with Search Bar and Filter Chips
        item(key = "header") {
          Box(modifier = Modifier.statusBarsPadding()) {
            HeaderSection(
              searchQuery = searchQuery,
              onSearchQueryChanged = { viewModel.setSearchQuery(it) },
              selectedGenre = selectedGenre,
              genreFilters = viewModel.genreFilters,
              onGenreSelected = { viewModel.selectGenre(it) },
              resultCount = filteredSongs.size
            )
          }
        }

        // Show Hero Banner only when not in active search to keep focus on search results
        if (searchQuery.isBlank()) {
          item(key = "hero_banner") {
            HeroBanner(
              onPlayAll = { viewModel.playAll() },
              onShuffleAll = { viewModel.shuffleAll() }
            )
          }

          // Quick Stream Horizontal Row (Loaded from Room Recent History)
          item(key = "recently_played") {
            RecentlyPlayedSection(
              songs = recentlyPlayed,
              currentSong = currentSong,
              isPlaying = playbackState.isPlaying,
              onSongSelected = { song -> viewModel.playSong(song) }
            )
          }
        } else {
          // Active Search Results Banner
          item(key = "search_results_banner") {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Search,
                  contentDescription = null,
                  tint = AuraViolet,
                  modifier = Modifier.size(18.dp)
                )
                Text(
                  text = "Results for \"$searchQuery\"",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = TextPrimary
                )
                Text(
                  text = "(${filteredSongs.size})",
                  style = MaterialTheme.typography.bodySmall,
                  color = AuraCyan
                )
              }

              TextButton(
                onClick = { viewModel.setSearchQuery("") },
                colors = ButtonDefaults.textButtonColors(contentColor = TextSecondary)
              ) {
                Text("Clear", fontSize = 12.sp)
              }
            }
          }
        }

        // Section Title: Tracks
        item(key = "tracks_header") {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Text(
                text = if (searchQuery.isNotEmpty()) "Matching Songs" else if (selectedGenre == "Favorites") "Favorite Songs" else "All Tracks",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  fontSize = 18.sp
                ),
                color = TextPrimary
              )
              Text(
                text = "(${filteredSongs.size})",
                style = MaterialTheme.typography.bodyMedium,
                color = TextTertiary
              )
            }

            if (filteredSongs.isNotEmpty()) {
              TextButton(
                onClick = { viewModel.playAll() },
                colors = ButtonDefaults.textButtonColors(contentColor = AuraViolet)
              ) {
                Text(
                  text = "Play All",
                  fontWeight = FontWeight.SemiBold,
                  fontSize = 13.sp
                )
              }
            }
          }
        }

        // Empty state when search or genre matches nothing
        if (filteredSongs.isEmpty()) {
          item(key = "empty_state") {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 48.dp, horizontal = 24.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = null,
                tint = TextTertiary,
                modifier = Modifier.size(54.dp)
              )
              Spacer(modifier = Modifier.height(12.dp))
              Text(
                text = if (searchQuery.isNotEmpty()) "No songs match \"$searchQuery\"" else "No songs in this filter",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = if (searchQuery.isNotEmpty()) "Check spelling or search by title / artist name" else "Try selecting 'All' to view all songs",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
              )
              Spacer(modifier = Modifier.height(16.dp))
              OutlinedButton(
                onClick = {
                  viewModel.setSearchQuery("")
                  viewModel.selectGenre("All")
                }
              ) {
                Icon(
                  imageVector = Icons.Default.Clear,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.size(6.dp))
                Text("Reset Filters")
              }
            }
          }
        } else {
          // List of Tracks
          itemsIndexed(
            items = filteredSongs,
            key = { _, song -> song.id }
          ) { index, song ->
            val isCurrent = currentSong?.id == song.id
            TrackListItem(
              song = song,
              index = index,
              isCurrent = isCurrent,
              isPlaying = isCurrent && playbackState.isPlaying,
              isFavorite = favorites.contains(song.id),
              onSongClick = { viewModel.playSong(song) },
              onFavoriteClick = { viewModel.toggleFavorite(song.id) },
              onAddToQueueClick = {
                // Add to queue feedback
              }
            )
          }
        }
      }

      // Full Screen Player Sheet (Slide Up Transition)
      AnimatedVisibility(
        visible = isPlayerExpanded && currentSong != null,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it })
      ) {
        currentSong?.let { song ->
          FullScreenPlayerSheet(
            song = song,
            playlist = playlist,
            isPlaying = playbackState.isPlaying,
            isBuffering = playbackState.isBuffering,
            currentPositionMs = playbackState.currentPositionMs,
            durationMs = playbackState.durationMs,
            isShuffleEnabled = playbackState.isShuffleEnabled,
            repeatMode = playbackState.repeatMode,
            isFavorite = favorites.contains(song.id),
            activeTab = activePlayerTab,
            playbackSpeed = playbackState.playbackSpeed,
            volume = playbackState.volume,
            sleepTimerMinutes = sleepTimerMinutes,
            onTabSelected = { viewModel.setActivePlayerTab(it) },
            onTogglePlayPause = { viewModel.togglePlayPause() },
            onPlayNext = { viewModel.playNext() },
            onPlayPrevious = { viewModel.playPrevious() },
            onSeekTo = { viewModel.seekTo(it) },
            onToggleShuffle = { viewModel.toggleShuffle() },
            onCycleRepeat = { viewModel.cycleRepeatMode() },
            onToggleFavorite = { viewModel.toggleFavorite(song.id) },
            onSelectSong = { viewModel.playSong(it) },
            onSetPlaybackSpeed = { viewModel.setPlaybackSpeed(it) },
            onSetVolume = { viewModel.setVolume(it) },
            onSetSleepTimer = { viewModel.setSleepTimer(it) },
            onCollapse = { viewModel.setPlayerExpanded(false) }
          )
        }
      }
    }
  }
}
