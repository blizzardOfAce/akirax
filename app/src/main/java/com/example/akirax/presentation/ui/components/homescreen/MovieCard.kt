package com.example.akirax.presentation.ui.components.homescreen

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.example.akirax.domain.model.Item
import com.example.akirax.domain.model.formatRating
import com.example.akirax.presentation.ui.components.RatingChip

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MovieCard(
    movie: Item.Movie,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onClick: (Item) -> Unit
) {
    with(sharedTransitionScope) {
        Card(
            modifier = Modifier
                .width(230.dp)
                .height(320.dp)
                .clip(RoundedCornerShape(8.dp))
                .padding(4.dp)
                .clickable { onClick(movie) },
            elevation = CardDefaults.cardElevation(2.dp),
        ) {
            Box {
                var isImageLoaded by remember { mutableStateOf(false) }

                // Animate the alpha value for a smooth fade-in
                val alpha by animateFloatAsState(
                    targetValue = if (isImageLoaded) 1f else 0f,
                    animationSpec = tween(durationMillis = 300),
                    label = ""
                )

                AsyncImage(
                    model = "https://image.tmdb.org/t/p/w500${movie.posterPath}",
                    contentDescription = movie.title,
                    modifier = Modifier
                        .sharedElement(
                            state =
                                rememberSharedContentState(key = "image/${movie.id}"),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                        .border(
                            width = 0.5.dp,
                            color = Color.DarkGray,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp))
                        .graphicsLayer { this.alpha = alpha }, // Apply animated alpha
                    contentScale = ContentScale.Crop,
                    onState = { state ->
                        if (state is AsyncImagePainter.State.Success) {
                            isImageLoaded = true
                        }
                    }
                )

                RatingChip(modifier = Modifier.align(Alignment.TopEnd), rating = formatRating(movie.voteAverage))
            }
        }
    }
}