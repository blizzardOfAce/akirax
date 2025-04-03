package com.example.akirax.presentation.ui.components.homescreen

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.akirax.R
import com.example.akirax.domain.model.Ticket
import com.example.akirax.presentation.ui.components.LanguageRibbon
import com.example.akirax.presentation.ui.theme.Poppins
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.delay

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ResaleTicketCarousel(
    tickets: List<Ticket>,
    onItemClicked: (Ticket) -> Unit
) {
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { tickets.size })

    LaunchedEffect(tickets.size) {
        if (tickets.isNotEmpty()) {
            while (true) {
                delay(3000)
                pagerState.animateScrollToPage(
                    (pagerState.currentPage + 1) % tickets.size,
                    animationSpec = tween(1000)
                )
            }
        }
    }
    if(tickets.isEmpty()){
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .padding(8.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(
                    width = 0.3.dp,
                    color = Color.DarkGray,
                    shape = RoundedCornerShape(8.dp)
                )
                .background(
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "No Resale Tickets Available",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground)
        }
    }

    else {
        Box(modifier = Modifier.fillMaxWidth()) {
            HorizontalPager(
                state = pagerState,
                pageSpacing = 4.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) { pageIndex ->
                val ticket = tickets[pageIndex]

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .padding(8.dp)
                        .clickable { onItemClicked(ticket) }
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    // Use the imageUrl directly from the ticket
                    AsyncImage(
                        model = "https://image.tmdb.org/t/p/w500${ticket.imageUrl}", // Default fallback if imageUrl is null
                        contentDescription = ticket.eventName,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.image_placeholder),
                        error = painterResource(R.drawable.image_placeholder)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        // Gradient Background
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.7f)
                                        )
                                    )
                                ),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                modifier = Modifier.padding(vertical = 4.dp, horizontal = 12.dp),
                                text = ticket.eventName,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 20.sp,
                                    fontFamily = Poppins,
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Text(
                                modifier = Modifier.padding(
                                    bottom = 6.dp,
                                    start = 12.dp,
                                    end = 12.dp
                                ),
                                text = "Resale Price: ₹${ticket.resalePrice}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }
                    LanguageRibbon(
                        language = ticket.language.uppercase(),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                    )
                }
            }
        }
    }
}

