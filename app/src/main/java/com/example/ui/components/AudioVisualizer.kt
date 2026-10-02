package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AuraCyan
import com.example.ui.theme.AuraPink
import com.example.ui.theme.AuraViolet
import kotlin.math.abs
import kotlin.math.sin

@Composable
fun AudioVisualizer(
  isPlaying: Boolean,
  modifier: Modifier = Modifier,
  barCount: Int = 5,
  maxHeight: Dp = 24.dp,
  barWidth: Dp = 3.dp,
  spacing: Dp = 2.dp,
  accentColors: List<Color> = listOf(AuraViolet, AuraPink, AuraCyan)
) {
  val totalWidth = (barWidth * barCount) + (spacing * (barCount - 1).coerceAtLeast(0))

  val transition = rememberInfiniteTransition(label = "visualizer_phase")
  val phase by if (isPlaying) {
    transition.animateFloat(
      initialValue = 0f,
      targetValue = (2 * Math.PI).toFloat(),
      animationSpec = infiniteRepeatable(
        animation = tween(durationMillis = 1200, easing = LinearEasing),
        repeatMode = RepeatMode.Restart
      ),
      label = "phase"
    )
  } else {
    remember { mutableFloatStateOf(0f) }
  }

  val gradientBrush = remember(accentColors) {
    Brush.verticalGradient(accentColors)
  }

  Canvas(
    modifier = modifier
      .width(totalWidth)
      .height(maxHeight)
  ) {
    val barWidthPx = barWidth.toPx()
    val spacingPx = spacing.toPx()
    val maxHeightPx = size.height

    for (i in 0 until barCount) {
      val xOffset = i * (barWidthPx + spacingPx)
      val heightFraction = if (isPlaying) {
        val wave = abs(sin(phase + (i * 0.85f)))
        0.25f + (0.75f * wave)
      } else {
        0.2f
      }

      val barHeightPx = (maxHeightPx * heightFraction).coerceIn(barWidthPx, maxHeightPx)
      val yOffset = maxHeightPx - barHeightPx

      drawRoundRect(
        brush = gradientBrush,
        topLeft = Offset(x = xOffset, y = yOffset),
        size = Size(width = barWidthPx, height = barHeightPx),
        cornerRadius = CornerRadius(barWidthPx / 2f, barWidthPx / 2f)
      )
    }
  }
}
