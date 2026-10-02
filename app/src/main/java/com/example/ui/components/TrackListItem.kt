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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun TrackListItem(
  song: Song,
  index: Int,
  isCurrent: Boolean,
  isPlaying: Boolean,
  isFavorite: Boolean,
  onSongClick: () -> Unit,
  onFavoriteClick: () -> Unit,
  onAddToQueueClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  var showMenu by remember { mutableStateOf(false) }

  val formattedDuration = remember(song.durationMs) {
    val totalSec = song.durationMs / 1000
    val min = totalSec / 60
    val sec = totalSec % 60
    String.format("%d:%02d", min, sec)
  }

  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 4.dp)
      .clip(RoundedCornerShape(14.dp))
      .background(if (isCurrent) DarkSurfaceElevated else Color.Transparent)
      .border(
        width = if (isCurrent) 1.dp else 0.dp,
        color = if (isCurrent) AuraViolet.copy(alpha = 0.5f) else Color.Transparent,
        shape = RoundedCornerShape(14.dp)
      )
      .clickable { onSongClick() }
      .padding(horizontal = 10.dp, vertical = 10.dp)
      .testTag("track_item_${song.id}"),
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Number or live soundwave
    Box(
      modifier = Modifier
        .width(28.dp),
      contentAlignment = Alignment.Center
    ) {
      if (isCurrent && isPlaying) {
        AudioVisualizer(
          isPlaying = true,
          barCount = 3,
          maxHeight = 18.dp,
          barWidth = 3.dp,
          spacing = 2.dp
        )
      } else {
        Text(
          text = String.format("%02d", index + 1),
          style = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.Bold
          ),
          color = if (isCurrent) AuraViolet else TextTertiary,
          fontSize = 13.sp
        )
      }
    }

    Spacer(modifier = Modifier.width(10.dp))

    // Album Artwork Thumbnail
    Box(
      modifier = Modifier
        .size(50.dp)
        .clip(RoundedCornerShape(10.dp))
        .background(DarkSurfaceVariant)
    ) {
      AsyncImage(
        model = song.albumArtUrl,
        contentDescription = "Artwork for ${song.title}",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
      )
    }

    Spacer(modifier = Modifier.width(14.dp))

    // Song info: Title, Artist, and Album
    Column(
      modifier = Modifier.weight(1f)
    ) {
      Text(
        text = song.title,
        style = MaterialTheme.typography.bodyMedium.copy(
          fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.SemiBold
        ),
        color = if (isCurrent) AuraViolet else TextPrimary,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = "${song.artist} • ${song.genre}",
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondary,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        fontSize = 12.sp
      )
    }

    // Duration
    Text(
      text = formattedDuration,
      style = MaterialTheme.typography.bodySmall,
      color = TextSecondary,
      fontSize = 12.sp,
      modifier = Modifier.padding(horizontal = 6.dp)
    )

    // Favorite heart toggle
    IconButton(
      onClick = onFavoriteClick,
      modifier = Modifier
        .size(38.dp)
        .testTag("favorite_button_${song.id}")
    ) {
      Icon(
        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
        contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
        tint = if (isFavorite) AuraPink else TextTertiary,
        modifier = Modifier.size(20.dp)
      )
    }

    // Context options menu
    Box {
      IconButton(
        onClick = { showMenu = true },
        modifier = Modifier
          .size(38.dp)
          .testTag("track_menu_${song.id}")
      ) {
        Icon(
          imageVector = Icons.Default.MoreVert,
          contentDescription = "Song options",
          tint = TextSecondary,
          modifier = Modifier.size(18.dp)
        )
      }

      DropdownMenu(
        expanded = showMenu,
        onDismissRequest = { showMenu = false },
        modifier = Modifier
          .background(DarkSurfaceElevated)
          .border(1.dp, DarkCardBorder, RoundedCornerShape(8.dp))
      ) {
        DropdownMenuItem(
          text = { Text("Play Now", color = TextPrimary) },
          onClick = {
            showMenu = false
            onSongClick()
          }
        )
        DropdownMenuItem(
          text = { Text("Add to Queue", color = TextPrimary) },
          onClick = {
            showMenu = false
            onAddToQueueClick()
          }
        )
        DropdownMenuItem(
          text = { Text(if (isFavorite) "Favorited ❤️" else "Favorite", color = TextPrimary) },
          onClick = {
            showMenu = false
            onFavoriteClick()
          }
        )
      }
    }
  }
}
