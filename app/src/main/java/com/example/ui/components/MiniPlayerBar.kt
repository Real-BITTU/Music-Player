package com.example.ui.components

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.Song
import com.example.ui.theme.AuraCyan
import com.example.ui.theme.AuraPink
import com.example.ui.theme.AuraViolet
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun MiniPlayerBar(
  currentSong: Song?,
  isPlaying: Boolean,
  isBuffering: Boolean,
  currentPositionMs: Long,
  durationMs: Long,
  isFavorite: Boolean,
  onTogglePlayPause: () -> Unit,
  onPlayNext: () -> Unit,
  onToggleFavorite: () -> Unit,
  onExpandPlayer: () -> Unit,
  modifier: Modifier = Modifier
) {
  if (currentSong == null) return

  val progressFraction = if (durationMs > 0) {
    (currentPositionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
  } else 0f

  Box(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 12.dp, vertical = 6.dp)
      .shadow(16.dp, RoundedCornerShape(18.dp))
      .clip(RoundedCornerShape(18.dp))
      .background(DarkSurfaceElevated)
      .border(1.dp, DarkCardBorder, RoundedCornerShape(18.dp))
      .clickable { onExpandPlayer() }
      .testTag("mini_player_bar")
  ) {
    Column {
      // Micro Top Progress Indicator
      LinearProgressIndicator(
        progress = { progressFraction },
        modifier = Modifier
          .fillMaxWidth()
          .height(3.dp),
        color = AuraViolet,
        trackColor = DarkSurfaceVariant
      )

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Thumbnail with subtle rounded border
        Box(
          modifier = Modifier
            .size(46.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(DarkSurfaceVariant)
        ) {
          AsyncImage(
            model = currentSong.albumArtUrl,
            contentDescription = "Mini player artwork",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Title & Artist with Live Visualizer or Buffering indicator
        Column(
          modifier = Modifier.weight(1f)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Text(
              text = currentSong.title,
              style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold
              ),
              color = TextPrimary,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )

            if (isPlaying) {
              AudioVisualizer(
                isPlaying = true,
                barCount = 3,
                maxHeight = 12.dp,
                barWidth = 2.dp,
                spacing = 1.5.dp
              )
            }
          }

          Spacer(modifier = Modifier.height(2.dp))

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Text(
              text = currentSong.artist,
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondary,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              fontSize = 12.sp
            )

            if (isBuffering) {
              Text(
                text = "• Buffering...",
                style = MaterialTheme.typography.bodySmall,
                color = AuraCyan,
                fontSize = 11.sp
              )
            }
          }
        }

        // Action Buttons: Favorite, Play/Pause, Skip Next
        IconButton(
          onClick = onToggleFavorite,
          modifier = Modifier
            .size(42.dp)
            .testTag("mini_favorite_button")
        ) {
          Icon(
            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = "Favorite",
            tint = if (isFavorite) AuraPink else TextTertiary,
            modifier = Modifier.size(20.dp)
          )
        }

        // Circular Play/Pause button
        Box(
          modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(
              Brush.linearGradient(listOf(AuraViolet, AuraPink))
            )
            .clickable { onTogglePlayPause() }
            .testTag("mini_play_pause_button"),
          contentAlignment = Alignment.Center
        ) {
          if (isBuffering) {
            CircularProgressIndicator(
              modifier = Modifier.size(20.dp),
              color = Color.White,
              strokeWidth = 2.dp
            )
          } else {
            Icon(
              imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
              contentDescription = if (isPlaying) "Pause" else "Play",
              tint = Color.White,
              modifier = Modifier.size(24.dp)
            )
          }
        }

        Spacer(modifier = Modifier.width(4.dp))

        IconButton(
          onClick = onPlayNext,
          modifier = Modifier
            .size(42.dp)
            .testTag("mini_next_button")
        ) {
          Icon(
            imageVector = Icons.Default.SkipNext,
            contentDescription = "Skip to next track",
            tint = TextPrimary,
            modifier = Modifier.size(24.dp)
          )
        }
      }
    }
  }
}
