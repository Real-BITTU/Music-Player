package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
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
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun RecentlyPlayedSection(
  songs: List<Song>,
  currentSong: Song?,
  isPlaying: Boolean,
  onSongSelected: (Song) -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 8.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Quick Stream",
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Bold,
          fontSize = 17.sp
        ),
        color = TextPrimary
      )
      Text(
        text = "${songs.size} tracks",
        style = MaterialTheme.typography.labelMedium,
        color = AuraCyan
      )
    }

    Spacer(modifier = Modifier.height(10.dp))

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState())
        .padding(horizontal = 16.dp),
      horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      songs.forEach { song ->
        val isThisSongCurrent = currentSong?.id == song.id
        val isThisPlaying = isThisSongCurrent && isPlaying

        Box(
          modifier = Modifier
            .width(135.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurfaceElevated)
            .border(
              width = if (isThisSongCurrent) 1.5.dp else 1.dp,
              brush = if (isThisSongCurrent) {
                Brush.linearGradient(listOf(AuraViolet, AuraPink))
              } else {
                Brush.linearGradient(listOf(DarkCardBorder, DarkCardBorder))
              },
              shape = RoundedCornerShape(16.dp)
            )
            .clickable { onSongSelected(song) }
            .padding(10.dp)
            .testTag("recent_card_${song.id}")
        ) {
          Column {
            // Album art with quick play button overlay
            Box(
              modifier = Modifier
                .size(115.dp)
                .clip(RoundedCornerShape(12.dp))
            ) {
              AsyncImage(
                model = song.albumArtUrl,
                contentDescription = "Cover for ${song.title}",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
              )

              // Circular play/pause badge on bottom right
              Box(
                modifier = Modifier
                  .align(Alignment.BottomEnd)
                  .padding(6.dp)
                  .size(34.dp)
                  .clip(CircleShape)
                  .background(
                    if (isThisPlaying) AuraPink else AuraViolet.copy(alpha = 0.9f)
                  )
                  .shadow(4.dp, CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = if (isThisPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                  contentDescription = if (isThisPlaying) "Pause" else "Play",
                  tint = Color.White,
                  modifier = Modifier.size(20.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = song.title,
              style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold
              ),
              color = if (isThisSongCurrent) AuraViolet else TextPrimary,
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
        }
      }
    }
  }
}
