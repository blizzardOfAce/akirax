package com.example.akirax.presentation.ui.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.akirax.domain.model.Item
import com.example.akirax.domain.model.formatRating
import com.example.akirax.presentation.ui.components.homescreen.ShimmerCard
import com.example.akirax.presentation.ui.components.moviedetailsscreen.Chip
import com.example.akirax.presentation.ui.components.moviedetailsscreen.ExpandableText
import com.example.akirax.presentation.ui.components.moviedetailsscreen.InfoCard
import com.example.akirax.presentation.ui.components.moviedetailsscreen.ResellDialog
import com.example.akirax.presentation.viewmodel.HomeViewModel
import com.example.akirax.presentation.viewmodel.TicketsViewModel
import kotlinx.coroutines.launch


private const val MOVIE_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MovieDetailsScreen(
    contentPadding: PaddingValues,
    movieId: String,
    homeViewModel: HomeViewModel,
    onClickSelectSeat: () -> Unit,
   // navController: NavController,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    ticketsViewModel: TicketsViewModel
) {
    val movies by homeViewModel.state.collectAsStateWithLifecycle()
    val movieDetails = movies.movies.find { it.id == movieId }


    val ticketHash by ticketsViewModel.ticketHash.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    val boughtTickets by ticketsViewModel.boughtTickets.collectAsState()
    val hasBoughtTicket = remember { mutableStateOf(false) }

    LaunchedEffect(boughtTickets) {
        hasBoughtTicket.value = movieDetails?.id in boughtTickets
        if(hasBoughtTicket.value){
            ticketsViewModel.fetchTicketHashByEventId(eventId = movieDetails?.id ?: "NULL")
        }
    }

    LaunchedEffect(movieId) {
        if (movies.movies.none { it.id == movieId }) { // Only fetch if not found
            homeViewModel.getMovieDetails(movieId.toInt())
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        when {
            movies.isLoading -> {
                ShimmerMovieDetails(contentPadding = contentPadding)
            }

            movieDetails != null -> {
                MovieDetailsContent(
                    movieDetails = movieDetails,
                    onClickSelectSeat = onClickSelectSeat,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope,
                    contentPadding = contentPadding,
                    hasBoughtTicket = hasBoughtTicket.value,
                    onConfirm = { price ->
                        if (ticketHash != null) {
                            coroutineScope.launch {
                                try {
                                    ticketsViewModel.markTicketForResale(
                                        ticketHash = ticketHash ?: "",
                                        resalePrice = price.toDouble()
                                    )
                                } catch (e: Exception) {
                                    Log.e("MovieDetails", "Error in marking ticket for resale: ${e.message}")
                                }
                            }
                        } else {
                            Log.e("MovieDetails", "Ticket hash is null!")
                        }
                    }
                )
            }
            movies.error != null -> {
                Text("Error: ${movies.error}", color = Color.Red)
            }
            else -> {
                Text("Failed to load movie details", color = Color.Red)
            }
        }

    }
}

@Composable
fun ShimmerMovieDetails(contentPadding: PaddingValues) {
    Column(
        modifier = Modifier.fillMaxSize().padding(contentPadding)
            .padding(12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically) {
            ShimmerCard(height = 360.dp, width = 300.dp)
            Column(modifier = Modifier.wrapContentHeight(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {
                repeat(3) {
                    ShimmerCard(height = 80.dp, width = 100.dp)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
        ShimmerCard(height = 48.dp, width = 300.dp)
        Spacer(modifier = Modifier.height(16.dp))
        ShimmerCard(height = 200.dp, width = 500.dp)
        Spacer(modifier = Modifier.height(8.dp))
        ShimmerCard(height = 48.dp, width = 300.dp)
        Spacer(modifier = Modifier.height(240.dp))
        ShimmerCard(height = 56.dp, width = 500.dp)

    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MovieDetailsContent(
    onClickSelectSeat: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    movieDetails: Item.Movie,
    contentPadding: PaddingValues,
    hasBoughtTicket: Boolean,
    onConfirm: (String) -> Unit
) {
    val showResellDialog = remember { mutableStateOf(false) }

    with(sharedTransitionScope) {
        val fullImageUrl = MOVIE_IMAGE_BASE_URL + movieDetails.posterPath

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp)
            ) {
                // Movie Details Content
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AsyncImage(
                        model = fullImageUrl,
                        contentDescription = movieDetails.title,
                        modifier = Modifier
                            .sharedElement(
                                state =
                                    rememberSharedContentState(key = "image/${movieDetails.id}"),
                                animatedVisibilityScope = animatedVisibilityScope
                            )
                            .height(350.dp)
                            .aspectRatio(0.85f)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Column(
                        modifier = Modifier.fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        InfoCard(
                            Icons.Default.Star,
                            "Rating",
                            formatRating(movieDetails.voteAverage)
                        )
                        InfoCard(
                            Icons.Default.AccessTime,
                            "Runtime",
                            movieDetails.runtime.toString()
                        )
                        InfoCard(
                            Icons.Default.Language,
                            "Language",
                            movieDetails.originalLanguage ?: "Unknown"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = movieDetails.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                ExpandableText(text = movieDetails.overview ?: "", maxLength = 200)
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "Cast",
                    modifier = Modifier.padding(start = 4.dp),
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow {
                    items(movieDetails.cast) { castMember ->
                        Chip(text = castMember)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Additional Info",
                    modifier = Modifier.padding(start = 4.dp),
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column {
                    Text(
                        "Release Date: ${movieDetails.releaseDate}",
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    Text(
                        "Genres: ${movieDetails.genres?.joinToString(", ")}",
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(128.dp))
            }

            // Sticky Buttons (Absolutely Positioned)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(vertical = 20.dp, horizontal = 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Button(
                        onClick = onClickSelectSeat,
                            //{ navController.navigate("seat_selection/${movieDetails.id}") },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Select Seat", fontSize = 20.sp)
                    }

                    if (hasBoughtTicket) {
                        OutlinedButton(
                            onClick = { showResellDialog.value = true },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Resell Ticket", fontSize = 20.sp)
                        }
                    }
                }
            }
        }
    }

    if (showResellDialog.value) {
        ResellDialog(
            onDismiss = { showResellDialog.value = false },
            onConfirm = onConfirm
        )
    }
}







