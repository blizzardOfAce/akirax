package com.example.akirax.presentation.ui.components.homescreen

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ShimmerCard(height: Dp, width: Dp) {
    val transition = rememberInfiniteTransition(label = "")

    // Animate the shimmer's horizontal position
    val shimmerTranslateX by transition.animateFloat(
        initialValue = -width.value, // Start outside on the left
        targetValue = width.value * 2, // Move to the right edge and beyond
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )

    // Define shimmer colors for a horizontal gradient effect
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
        Color.DarkGray.copy(alpha = 0.6f),
        MaterialTheme.colorScheme.background.copy(alpha = 0.5f)
    )

    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .width(width)
            .height(height)
            .padding(4.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(width = 0.3.dp, color = Color.DarkGray, shape = RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors()
            .copy(containerColor = MaterialTheme.colorScheme.background)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Create a horizontal gradient that moves across the card
            val shimmerBrush = Brush.linearGradient(
                colors = shimmerColors,
                start = Offset(shimmerTranslateX, 0f),
                end = Offset(shimmerTranslateX + (width.value * 2f), 0f) // Keep Y-axis constant
            )

            drawRoundRect(
                brush = shimmerBrush,
                cornerRadius = CornerRadius(8.dp.toPx())
            )
        }
    }
}