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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AuraCyan
import com.example.ui.theme.AuraPink
import com.example.ui.theme.AuraViolet
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Calendar

@Composable
fun HeaderSection(
  searchQuery: String,
  onSearchQueryChanged: (String) -> Unit,
  selectedGenre: String,
  genreFilters: List<String>,
  onGenreSelected: (String) -> Unit,
  resultCount: Int,
  modifier: Modifier = Modifier
) {
  val greeting = remember {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    when (hour) {
      in 4..11 -> "Good morning"
      in 12..17 -> "Good afternoon"
      else -> "Good evening"
    }
  }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp)
  ) {
    // Top Row: Avatar, Greeting, App Brand
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // Gradient Profile Avatar
        Box(
          modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(
              Brush.linearGradient(listOf(AuraViolet, AuraPink))
            ),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Headphones,
            contentDescription = "User profile icon",
            tint = Color.White,
            modifier = Modifier.size(22.dp)
          )
        }

        Column {
          Text(
            text = greeting,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            fontSize = 12.sp
          )
          Text(
            text = "Aura Music Player",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 18.sp
            ),
            color = TextPrimary
          )
        }
      }

      // Offline / Local Storage Status Badge
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .background(DarkSurfaceVariant)
          .border(1.dp, DarkCardBorder, RoundedCornerShape(12.dp))
          .padding(horizontal = 10.dp, vertical = 6.dp)
      ) {
        Box(
          modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(AuraCyan)
        )
        Text(
          text = "Local Room DB",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
          color = AuraCyan,
          fontSize = 11.sp
        )
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Interactive Search Bar (Title or Artist)
    TextField(
      value = searchQuery,
      onValueChange = onSearchQueryChanged,
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(14.dp))
        .border(
          width = if (searchQuery.isNotEmpty()) 1.5.dp else 1.dp,
          brush = if (searchQuery.isNotEmpty()) {
            Brush.horizontalGradient(listOf(AuraViolet, AuraCyan))
          } else {
            Brush.horizontalGradient(listOf(DarkCardBorder, DarkCardBorder))
          },
          shape = RoundedCornerShape(14.dp)
        )
        .testTag("search_input"),
      placeholder = {
        Text(
          text = "Search songs by title or artist...",
          color = TextSecondary,
          fontSize = 14.sp
        )
      },
      leadingIcon = {
        Icon(
          imageVector = Icons.Default.Search,
          contentDescription = "Search icon",
          tint = if (searchQuery.isNotEmpty()) AuraViolet else AuraVioletLight
        )
      },
      trailingIcon = {
        if (searchQuery.isNotEmpty()) {
          IconButton(
            onClick = { onSearchQueryChanged("") },
            modifier = Modifier.testTag("clear_search_button")
          ) {
            Icon(
              imageVector = Icons.Default.Clear,
              contentDescription = "Clear search",
              tint = TextSecondary
            )
          }
        }
      },
      singleLine = true,
      colors = TextFieldDefaults.colors(
        focusedContainerColor = DarkSurfaceElevated,
        unfocusedContainerColor = DarkSurfaceVariant,
        disabledContainerColor = DarkSurfaceVariant,
        cursorColor = AuraViolet,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary
      )
    )

    // Filter Chips: All, Favorites, Genres
    Spacer(modifier = Modifier.height(12.dp))

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      genreFilters.forEach { genre ->
        val isSelected = genre.equals(selectedGenre, ignoreCase = true)
        val isFavoritesChip = genre == "Favorites"

        val chipBackground = when {
          isSelected && isFavoritesChip -> Brush.horizontalGradient(listOf(AuraPink, AuraViolet))
          isSelected -> Brush.horizontalGradient(listOf(AuraViolet, AuraCyan))
          else -> Brush.horizontalGradient(listOf(DarkSurfaceVariant, DarkSurfaceVariant))
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(chipBackground)
            .border(
              width = 1.dp,
              color = if (isSelected) Color.Transparent else DarkCardBorder,
              shape = RoundedCornerShape(20.dp)
            )
            .clickable { onGenreSelected(genre) }
            .padding(horizontal = 14.dp, vertical = 7.dp)
            .testTag("genre_chip_$genre"),
          contentAlignment = Alignment.Center
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            if (isFavoritesChip) {
              Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = if (isSelected) Color.White else AuraPink,
                modifier = Modifier.size(13.dp)
              )
            }
            Text(
              text = genre,
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
              ),
              color = if (isSelected) Color.White else TextSecondary,
              fontSize = 12.sp
            )
          }
        }
      }
    }
  }
}

private val AuraVioletLight = Color(0xFFA78BFA)
