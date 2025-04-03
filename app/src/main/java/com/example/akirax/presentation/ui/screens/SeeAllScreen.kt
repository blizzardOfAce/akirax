package com.example.akirax.presentation.ui.screens

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.example.akirax.Screen
import com.example.akirax.domain.model.Item
import com.example.akirax.domain.model.formatRating
import com.example.akirax.presentation.ui.components.RatingChip
import com.example.akirax.presentation.ui.components.homescreen.EventCard
import com.example.akirax.presentation.viewmodel.SeeAllViewModel
import org.koin.androidx.compose.getViewModel

private const val MOVIE_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SeeAllScreen(
    viewModel: SeeAllViewModel = getViewModel(),
    contentPadding: PaddingValues,
    contentType: String,
    onClickMovieCard: (String) -> Unit
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val gridState = rememberLazyGridState()

    LaunchedEffect(contentType) {
        viewModel.loadInitial(contentType)
    }

//    Will be implemented later if the API has it
//    LaunchedEffect(gridState) {
//        snapshotFlow { gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
//            .filter { it != null && it >= (gridState.layoutInfo.totalItemsCount - 6) }
//            .distinctUntilChanged()
//            .collect { viewModel.loadMore(contentType) }
//    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(4.dp)
    ) {
        Text(
            text = when (contentType) {
                "Movies" -> "Popular Movies"
                "Sports" -> "Sports Events"
                "Music" -> "Music Events"
                "Venues" -> "Venues"
                "Attractions" -> "Attractions"
                else -> "Events"
            },
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(start = 12.dp, bottom = 16.dp)
        )
        if (uiState.value.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(64.dp)
                        .padding(16.dp)
                )

            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            state = gridState,
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            when (contentType) {
                "Movies" -> {
                    items(uiState.value.movies, key = { it.id }) { movie ->
                        MovieCardWithoutTransition(
                            movie = movie,
                            onClick = { onClickMovieCard(movie.id) }
                        )
                    }
                }

                "Sports", "Music" -> {
                    items(uiState.value.events, key = { it.id }) { event ->
                        EventCard(event = event)
                    }
                }
            }

        }
    }
}

@Composable
fun MovieCardWithoutTransition(
    movie: Item.Movie,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(230.dp)
            .height(320.dp)
            .clip(RoundedCornerShape(8.dp))
            .padding(4.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(2.dp)
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
                model = "$MOVIE_IMAGE_BASE_URL${movie.posterPath}",
                contentDescription = movie.title,
                modifier = Modifier
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
                        isImageLoaded = true // Mark as loaded when image is fetched
                    }
                }
            )

            RatingChip(
                modifier = Modifier.align(Alignment.TopEnd),
                rating = formatRating(movie.voteAverage)
            )
        }
    }
}



