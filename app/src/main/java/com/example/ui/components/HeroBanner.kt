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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.AuraCyan
import com.example.ui.theme.AuraPink
import com.example.ui.theme.AuraViolet
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun HeroBanner(
  onPlayAll: () -> Unit,
  onShuffleAll: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp)
      .height(180.dp)
      .clip(RoundedCornerShape(20.dp))
      .border(1.dp, DarkCardBorder, RoundedCornerShape(20.dp))
  ) {
    // Backdrop Image
    AsyncImage(
      model = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=900&auto=format&fit=crop&q=80",
      contentDescription = "Featured Mix Banner Background",
      contentScale = ContentScale.Crop,
      modifier = Modifier.fillMaxSize()
    )

    // Deep gradient overlay for high contrast readability
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          Brush.horizontalGradient(
            colors = listOf(
              DarkSurface.copy(alpha = 0.95f),
              DarkSurface.copy(alpha = 0.75f),
              Color.Transparent
            )
          )
        )
    )

    // Content inside banner
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(18.dp),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Column {
        // Tag badge
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(AuraViolet.copy(alpha = 0.25f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = AuraPink,
            modifier = Modifier.size(12.dp)
          )
          Text(
            text = "FEATURED MIX",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            ),
            color = AuraPink,
            fontSize = 10.sp
          )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = "Cyber Aura Vibes",
          style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.ExtraBold
          ),
          color = TextPrimary
        )

        Text(
          text = "Streaming 5 high-fidelity tracks curated for you",
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondary,
          maxLines = 1
        )
      }

      // Action buttons: Play All & Shuffle
      Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Button(
          onClick = onPlayAll,
          colors = ButtonDefaults.buttonColors(
            containerColor = AuraViolet,
            contentColor = Color.White
          ),
          shape = RoundedCornerShape(24.dp),
          modifier = Modifier.testTag("hero_play_all_button")
        ) {
          Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "Play all tracks",
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Play All",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          )
        }

        OutlinedButton(
          onClick = onShuffleAll,
          colors = ButtonDefaults.outlinedButtonColors(
            contentColor = TextPrimary
          ),
          border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = Brush.horizontalGradient(listOf(AuraCyan, AuraViolet))
          ),
          shape = RoundedCornerShape(24.dp),
          modifier = Modifier.testTag("hero_shuffle_button")
        ) {
          Icon(
            imageVector = Icons.Default.Shuffle,
            contentDescription = "Shuffle tracks",
            tint = AuraCyan,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Shuffle",
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp
          )
        }
      }
    }
  }
}
