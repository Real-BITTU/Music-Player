package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode as AnimRepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.FullPlayerTab
import com.example.model.RepeatMode
import com.example.model.Song
import com.example.ui.theme.AuraCyan
import com.example.ui.theme.AuraPink
import com.example.ui.theme.AuraViolet
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun FullScreenPlayerSheet(
  song: Song,
  playlist: List<Song>,
  isPlaying: Boolean,
  isBuffering: Boolean,
  currentPositionMs: Long,
  durationMs: Long,
  isShuffleEnabled: Boolean,
  repeatMode: RepeatMode,
  isFavorite: Boolean,
  activeTab: FullPlayerTab,
  playbackSpeed: Float,
  volume: Float,
  sleepTimerMinutes: Int?,
  onTabSelected: (FullPlayerTab) -> Unit,
  onTogglePlayPause: () -> Unit,
  onPlayNext: () -> Unit,
  onPlayPrevious: () -> Unit,
  onSeekTo: (Long) -> Unit,
  onToggleShuffle: () -> Unit,
  onCycleRepeat: () -> Unit,
  onToggleFavorite: () -> Unit,
  onSelectSong: (Song) -> Unit,
  onSetPlaybackSpeed: (Float) -> Unit,
  onSetVolume: (Float) -> Unit,
  onSetSleepTimer: (Int?) -> Unit,
  onCollapse: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler {
    onCollapse()
  }

  var isUserScrubbing by remember { mutableStateOf(false) }
  var scrubPositionMs by remember { mutableFloatStateOf(0f) }
  var showSpeedMenu by remember { mutableStateOf(false) }
  var showSleepTimerMenu by remember { mutableStateOf(false) }

  val effectivePosition = if (isUserScrubbing) {
    scrubPositionMs.toLong()
  } else {
    currentPositionMs
  }

  val maxDuration = if (durationMs > 0) durationMs else song.durationMs.coerceAtLeast(1000L)

  fun formatMs(ms: Long): String {
    val totalSec = (ms / 1000).coerceAtLeast(0)
    val minutes = totalSec / 60
    val seconds = totalSec % 60
    return String.format("%d:%02d", minutes, seconds)
  }

  Surface(
    modifier = modifier
      .fillMaxSize()
      .statusBarsPadding()
      .navigationBarsPadding()
      .testTag("fullscreen_player_sheet"),
    color = DarkBg
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 20.dp, vertical = 6.dp)
    ) {
      // Top Drag Handle Indicator
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp),
        contentAlignment = Alignment.Center
      ) {
        Box(
          modifier = Modifier
            .width(42.dp)
            .height(4.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(TextTertiary.copy(alpha = 0.5f))
        )
      }

      // Top Navigation Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = onCollapse,
          modifier = Modifier.testTag("player_collapse_button")
        ) {
          Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = "Collapse full player",
            tint = TextPrimary,
            modifier = Modifier.size(32.dp)
          )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.GraphicEq,
              contentDescription = null,
              tint = AuraCyan,
              modifier = Modifier.size(13.dp)
            )
            Text(
              text = "HI-RES AUDIO • 320 KBPS",
              style = MaterialTheme.typography.labelSmall.copy(
                letterSpacing = 1.2.sp,
                fontWeight = FontWeight.Bold
              ),
              color = AuraCyan,
              fontSize = 10.sp
            )
          }

          Text(
            text = song.album,
            style = MaterialTheme.typography.bodyMedium.copy(
              fontWeight = FontWeight.SemiBold
            ),
            color = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        IconButton(
          onClick = { showSpeedMenu = true },
          modifier = Modifier.testTag("player_more_options")
        ) {
          Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = "More player options",
            tint = TextPrimary,
            modifier = Modifier.size(24.dp)
          )
        }
      }

      // Tab Switcher: Now Playing / Queue / Lyrics
      TabRow(
        selectedTabIndex = activeTab.ordinal,
        containerColor = Color.Transparent,
        contentColor = AuraViolet,
        indicator = { tabPositions ->
          if (activeTab.ordinal < tabPositions.size) {
            TabRowDefaults.SecondaryIndicator(
              modifier = Modifier.tabIndicatorOffset(tabPositions[activeTab.ordinal]),
              color = AuraViolet,
              height = 3.dp
            )
          }
        },
        divider = {}
      ) {
        FullPlayerTab.values().forEach { tab ->
          val label = when (tab) {
            FullPlayerTab.NOW_PLAYING -> "Now Playing"
            FullPlayerTab.QUEUE -> "Queue (${playlist.size})"
            FullPlayerTab.LYRICS -> "Lyrics"
          }
          Tab(
            selected = activeTab == tab,
            onClick = { onTabSelected(tab) },
            text = {
              Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = if (activeTab == tab) FontWeight.Bold else FontWeight.Normal,
                color = if (activeTab == tab) Color.White else TextSecondary
              )
            },
            modifier = Modifier.testTag("tab_${tab.name}")
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Tab Content
      when (activeTab) {
        FullPlayerTab.NOW_PLAYING -> {
          NowPlayingTabEnhanced(
            song = song,
            isPlaying = isPlaying,
            isBuffering = isBuffering,
            effectivePosition = effectivePosition,
            maxDuration = maxDuration,
            formatMs = ::formatMs,
            isUserScrubbing = isUserScrubbing,
            onScrubbingChange = { isUserScrubbing = it },
            scrubPositionMs = scrubPositionMs,
            onScrubPositionChange = { scrubPositionMs = it },
            isShuffleEnabled = isShuffleEnabled,
            repeatMode = repeatMode,
            isFavorite = isFavorite,
            playbackSpeed = playbackSpeed,
            volume = volume,
            sleepTimerMinutes = sleepTimerMinutes,
            onTogglePlayPause = onTogglePlayPause,
            onPlayNext = onPlayNext,
            onPlayPrevious = onPlayPrevious,
            onSeekTo = onSeekTo,
            onToggleShuffle = onToggleShuffle,
            onCycleRepeat = onCycleRepeat,
            onToggleFavorite = onToggleFavorite,
            onSetPlaybackSpeed = onSetPlaybackSpeed,
            onSetVolume = onSetVolume,
            onSetSleepTimer = onSetSleepTimer
          )
        }

        FullPlayerTab.QUEUE -> {
          QueueTabContent(
            currentSong = song,
            playlist = playlist,
            onSelectSong = onSelectSong
          )
        }

        FullPlayerTab.LYRICS -> {
          LyricsTabContent(
            lyrics = song.lyrics,
            songTitle = song.title,
            currentProgressFraction = effectivePosition.toFloat() / maxDuration.toFloat()
          )
        }
      }
    }
  }
}

@Composable
private fun NowPlayingTabEnhanced(
  song: Song,
  isPlaying: Boolean,
  isBuffering: Boolean,
  effectivePosition: Long,
  maxDuration: Long,
  formatMs: (Long) -> String,
  isUserScrubbing: Boolean,
  onScrubbingChange: (Boolean) -> Unit,
  scrubPositionMs: Float,
  onScrubPositionChange: (Float) -> Unit,
  isShuffleEnabled: Boolean,
  repeatMode: RepeatMode,
  isFavorite: Boolean,
  playbackSpeed: Float,
  volume: Float,
  sleepTimerMinutes: Int?,
  onTogglePlayPause: () -> Unit,
  onPlayNext: () -> Unit,
  onPlayPrevious: () -> Unit,
  onSeekTo: (Long) -> Unit,
  onToggleShuffle: () -> Unit,
  onCycleRepeat: () -> Unit,
  onToggleFavorite: () -> Unit,
  onSetPlaybackSpeed: (Float) -> Unit,
  onSetVolume: (Float) -> Unit,
  onSetSleepTimer: (Int?) -> Unit
) {
  var showSpeedDropdown by remember { mutableStateOf(false) }
  var showSleepDropdown by remember { mutableStateOf(false) }

  // Subtle vinyl rotation animation when playing
  val infiniteTransition = rememberInfiniteTransition(label = "vinyl_rotate")
  val rotationDegree by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(22000, easing = LinearEasing),
      repeatMode = AnimRepeatMode.Restart
    ),
    label = "rotation"
  )

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState()),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    Spacer(modifier = Modifier.height(4.dp))

    // High Fidelity Artwork with Ambient Glow and Vinyl Accents
    Box(
      modifier = Modifier
        .size(260.dp)
        .drawBehind {
          drawCircle(
            brush = Brush.radialGradient(
              colors = listOf(
                AuraViolet.copy(alpha = if (isPlaying) 0.35f else 0.15f),
                AuraCyan.copy(alpha = if (isPlaying) 0.15f else 0.05f),
                Color.Transparent
              )
            ),
            radius = size.width * 0.7f
          )
        },
      contentAlignment = Alignment.Center
    ) {
      Box(
        modifier = Modifier
          .size(250.dp)
          .shadow(20.dp, RoundedCornerShape(24.dp))
          .clip(RoundedCornerShape(24.dp))
          .background(DarkSurfaceElevated)
          .border(1.dp, DarkCardBorder, RoundedCornerShape(24.dp))
      ) {
        AsyncImage(
          model = song.albumArtUrl,
          contentDescription = "Full artwork for ${song.title}",
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Animated Neon Equalizer Visualizer (20 bars)
    AudioVisualizer(
      isPlaying = isPlaying,
      barCount = 20,
      maxHeight = 32.dp,
      barWidth = 4.dp,
      spacing = 3.dp,
      modifier = Modifier.padding(vertical = 4.dp)
    )

    Spacer(modifier = Modifier.height(10.dp))

    // Track Title, Artist, and Favorite Heart
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = song.title,
          style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.ExtraBold,
            fontSize = 22.sp
          ),
          color = TextPrimary,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "${song.artist} • ${song.genre}",
          style = MaterialTheme.typography.bodyMedium,
          color = TextSecondary,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }

      IconButton(
        onClick = onToggleFavorite,
        modifier = Modifier
          .size(48.dp)
          .testTag("full_player_favorite_button")
      ) {
        Icon(
          imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
          contentDescription = "Toggle favorite",
          tint = if (isFavorite) AuraPink else TextSecondary,
          modifier = Modifier.size(28.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Quick Utility Chips: Speed (0.75x..2x) & Sleep Timer
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Speed Control Chip
      Box {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurfaceVariant)
            .border(1.dp, DarkCardBorder, RoundedCornerShape(16.dp))
            .clickable { showSpeedDropdown = true }
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .testTag("speed_chip")
        ) {
          Icon(
            imageVector = Icons.Default.Speed,
            contentDescription = null,
            tint = AuraCyan,
            modifier = Modifier.size(14.dp)
          )
          Text(
            text = "${playbackSpeed}x",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary
          )
        }

        DropdownMenu(
          expanded = showSpeedDropdown,
          onDismissRequest = { showSpeedDropdown = false },
          modifier = Modifier.background(DarkSurfaceElevated)
        ) {
          listOf(0.75f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { speed ->
            DropdownMenuItem(
              text = {
                Text(
                  text = "${speed}x Speed ${if (speed == playbackSpeed) "✓" else ""}",
                  color = if (speed == playbackSpeed) AuraCyan else TextPrimary
                )
              },
              onClick = {
                onSetPlaybackSpeed(speed)
                showSpeedDropdown = false
              }
            )
          }
        }
      }

      Spacer(modifier = Modifier.width(12.dp))

      // Sleep Timer Chip
      Box {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (sleepTimerMinutes != null) AuraViolet.copy(alpha = 0.25f) else DarkSurfaceVariant)
            .border(
              width = 1.dp,
              color = if (sleepTimerMinutes != null) AuraViolet else DarkCardBorder,
              shape = RoundedCornerShape(16.dp)
            )
            .clickable { showSleepDropdown = true }
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .testTag("sleep_timer_chip")
        ) {
          Icon(
            imageVector = Icons.Default.Bedtime,
            contentDescription = null,
            tint = if (sleepTimerMinutes != null) AuraViolet else TextSecondary,
            modifier = Modifier.size(14.dp)
          )
          Text(
            text = if (sleepTimerMinutes != null) "${sleepTimerMinutes}m left" else "Sleep Timer",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = if (sleepTimerMinutes != null) AuraViolet else TextSecondary
          )
        }

        DropdownMenu(
          expanded = showSleepDropdown,
          onDismissRequest = { showSleepDropdown = false },
          modifier = Modifier.background(DarkSurfaceElevated)
        ) {
          listOf(
            "Off" to null,
            "15 minutes" to 15,
            "30 minutes" to 30,
            "45 minutes" to 45,
            "60 minutes" to 60
          ).forEach { (label, minutes) ->
            DropdownMenuItem(
              text = { Text(label, color = TextPrimary) },
              onClick = {
                onSetSleepTimer(minutes)
                showSleepDropdown = false
              }
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Seeker Bar (Scrubbable Slider)
    Column(modifier = Modifier.fillMaxWidth()) {
      Slider(
        value = if (isUserScrubbing) scrubPositionMs else effectivePosition.toFloat(),
        onValueChange = { newValue ->
          onScrubbingChange(true)
          onScrubPositionChange(newValue)
        },
        onValueChangeFinished = {
          onScrubbingChange(false)
          onSeekTo(scrubPositionMs.toLong())
        },
        valueRange = 0f..maxDuration.toFloat(),
        colors = SliderDefaults.colors(
          thumbColor = Color.White,
          activeTrackColor = AuraViolet,
          inactiveTrackColor = DarkSurfaceVariant
        ),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("player_progress_slider")
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = formatMs(effectivePosition),
          style = MaterialTheme.typography.labelSmall,
          color = TextSecondary
        )
        Text(
          text = formatMs(maxDuration),
          style = MaterialTheme.typography.labelSmall,
          color = TextSecondary
        )
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Playback Controls Row: Shuffle, Prev, Play/Pause, Next, Repeat
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp),
      horizontalArrangement = Arrangement.SpaceEvenly,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Shuffle
      IconButton(
        onClick = onToggleShuffle,
        modifier = Modifier
          .size(48.dp)
          .testTag("player_shuffle_button")
      ) {
        Icon(
          imageVector = Icons.Default.Shuffle,
          contentDescription = "Shuffle",
          tint = if (isShuffleEnabled) AuraCyan else TextTertiary,
          modifier = Modifier.size(24.dp)
        )
      }

      // Previous
      IconButton(
        onClick = onPlayPrevious,
        modifier = Modifier
          .size(48.dp)
          .testTag("player_prev_button")
      ) {
        Icon(
          imageVector = Icons.Default.SkipPrevious,
          contentDescription = "Previous track",
          tint = TextPrimary,
          modifier = Modifier.size(36.dp)
        )
      }

      // Large Circular Play/Pause Button
      Box(
        modifier = Modifier
          .size(70.dp)
          .clip(CircleShape)
          .background(
            Brush.linearGradient(listOf(AuraViolet, AuraPink))
          )
          .clickable { onTogglePlayPause() }
          .shadow(12.dp, CircleShape)
          .testTag("player_play_pause_button"),
        contentAlignment = Alignment.Center
      ) {
        if (isBuffering) {
          CircularProgressIndicator(
            modifier = Modifier.size(30.dp),
            color = Color.White,
            strokeWidth = 3.dp
          )
        } else {
          Icon(
            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = if (isPlaying) "Pause" else "Play",
            tint = Color.White,
            modifier = Modifier.size(38.dp)
          )
        }
      }

      // Next
      IconButton(
        onClick = onPlayNext,
        modifier = Modifier
          .size(48.dp)
          .testTag("player_next_button")
      ) {
        Icon(
          imageVector = Icons.Default.SkipNext,
          contentDescription = "Next track",
          tint = TextPrimary,
          modifier = Modifier.size(36.dp)
        )
      }

      // Repeat (Off, All, One)
      IconButton(
        onClick = onCycleRepeat,
        modifier = Modifier
          .size(48.dp)
          .testTag("player_repeat_button")
      ) {
        val repeatTint = if (repeatMode != RepeatMode.OFF) AuraViolet else TextTertiary
        val repeatIcon = if (repeatMode == RepeatMode.ONE) Icons.Default.RepeatOne else Icons.Default.Repeat
        Icon(
          imageVector = repeatIcon,
          contentDescription = "Repeat: $repeatMode",
          tint = repeatTint,
          modifier = Modifier.size(24.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // In-Player Volume Slider
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      IconButton(
        onClick = { onSetVolume(0f) },
        modifier = Modifier.size(32.dp)
      ) {
        Icon(
          imageVector = if (volume <= 0.05f) Icons.Default.VolumeMute else Icons.Default.VolumeDown,
          contentDescription = "Mute volume",
          tint = TextSecondary,
          modifier = Modifier.size(20.dp)
        )
      }

      Slider(
        value = volume,
        onValueChange = onSetVolume,
        valueRange = 0f..1f,
        colors = SliderDefaults.colors(
          thumbColor = Color.White,
          activeTrackColor = AuraCyan,
          inactiveTrackColor = DarkSurfaceVariant
        ),
        modifier = Modifier.weight(1f)
      )

      IconButton(
        onClick = { onSetVolume(1f) },
        modifier = Modifier.size(32.dp)
      ) {
        Icon(
          imageVector = Icons.Default.VolumeUp,
          contentDescription = "Max volume",
          tint = TextSecondary,
          modifier = Modifier.size(20.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))
  }
}

@Composable
private fun QueueTabContent(
  currentSong: Song,
  playlist: List<Song>,
  onSelectSong: (Song) -> Unit
) {
  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    item {
      Text(
        text = "UP NEXT IN QUEUE",
        style = MaterialTheme.typography.labelSmall.copy(
          letterSpacing = 1.sp,
          fontWeight = FontWeight.Bold
        ),
        color = TextSecondary,
        modifier = Modifier.padding(vertical = 8.dp)
      )
    }

    itemsIndexed(playlist) { index, song ->
      val isCurrent = song.id == currentSong.id
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(if (isCurrent) DarkSurfaceElevated else DarkSurfaceVariant.copy(alpha = 0.5f))
          .border(
            width = if (isCurrent) 1.dp else 0.dp,
            color = if (isCurrent) AuraViolet else Color.Transparent,
            shape = RoundedCornerShape(12.dp)
          )
          .clickable { onSelectSong(song) }
          .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(8.dp))
        ) {
          AsyncImage(
            model = song.albumArtUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = song.title,
            style = MaterialTheme.typography.bodyMedium.copy(
              fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium
            ),
            color = if (isCurrent) AuraViolet else TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Text(
            text = song.artist,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        if (isCurrent) {
          Text(
            text = "Playing",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = AuraCyan,
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(AuraCyan.copy(alpha = 0.15f))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }
    }
  }
}

@Composable
private fun LyricsTabContent(
  lyrics: List<String>,
  songTitle: String,
  currentProgressFraction: Float
) {
  val activeLineIndex = if (lyrics.isNotEmpty()) {
    (currentProgressFraction * lyrics.size).toInt().coerceIn(0, lyrics.lastIndex)
  } else 0

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = "Lyrics • $songTitle",
      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
      color = TextSecondary,
      modifier = Modifier.padding(bottom = 24.dp)
    )

    lyrics.forEachIndexed { index, line ->
      val isActive = index == activeLineIndex
      Text(
        text = line,
        style = MaterialTheme.typography.bodyLarge.copy(
          fontSize = if (isActive) 22.sp else 18.sp,
          fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Medium,
          lineHeight = 32.sp
        ),
        color = if (isActive) Color.White else TextTertiary,
        textAlign = TextAlign.Center,
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 8.dp)
      )
    }

    Spacer(modifier = Modifier.height(40.dp))
  }
}
