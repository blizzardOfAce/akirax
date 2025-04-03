package com.example.akirax.presentation.ui.screens


import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.akirax.Screen
import com.example.akirax.domain.model.Item
import com.example.akirax.presentation.ui.components.homescreen.AttractionCard
import com.example.akirax.presentation.ui.components.homescreen.EventCard
import com.example.akirax.presentation.ui.components.homescreen.MovieCard
import com.example.akirax.presentation.ui.components.homescreen.ResaleTicketCarousel
import com.example.akirax.presentation.ui.components.homescreen.SearchBarWithFilter
import com.example.akirax.presentation.ui.components.homescreen.SearchResultItem
import com.example.akirax.presentation.ui.components.homescreen.SectionWithSeeAll
import com.example.akirax.presentation.ui.components.homescreen.ShimmerCard
import com.example.akirax.presentation.ui.components.homescreen.VenueCard
import com.example.akirax.presentation.ui.components.homescreen.WelcomeSection
import com.example.akirax.presentation.viewmodel.HomeViewModel
import com.example.akirax.presentation.viewmodel.ProfileViewModel
import com.example.akirax.presentation.viewmodel.TicketsViewModel
import com.example.akirax.utils.HomeState

private const val DEFAULT_PROFILE_PIC =
    "https://t4.ftcdn.net/jpg/07/17/84/71/240_F_717847111_5dqQRbCOnSKiALUJzWHkjozKZAEQdVsf.jpg"

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    ticketsViewModel: TicketsViewModel,
    profileViewModel: ProfileViewModel,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    navController: NavController,
    onItemClick: (Item) -> Unit
) {


    val state by viewModel.state.collectAsState()
    val userState by profileViewModel.userState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val resaleTickets by ticketsViewModel.resaleTickets.collectAsState()

    var searchVisible by remember { mutableStateOf(true) }
    val categories = remember { listOf("All", "Sports", "Movies", "Venues", "Concerts") }
    var selectedCategory by remember { mutableStateOf(categories.first()) }

    val keyboardController = LocalSoftwareKeyboardController.current

    BackHandler(enabled = searchVisible && navController.currentBackStackEntry?.destination?.route == Screen.HomeScreen.route) {
        if (searchVisible) {
            searchVisible = false
            keyboardController?.hide()
        } else {
            navController.popBackStack()
        }
    }

    LaunchedEffect(Unit){
    if(state.movies.isEmpty()){
        Log.d("Homescreen: ", "loadcontent ran")
        viewModel.loadContent()
    }
    }

    LaunchedEffect(selectedCategory) {
        if (selectedCategory != "All") {
            viewModel.filterItemsByCategory(selectedCategory)
        }
    }

    var hasFetchedOnce by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (resaleTickets.isEmpty() && !hasFetchedOnce) {
            hasFetchedOnce = true
            ticketsViewModel.fetchResaleTickets()
        }
    }

    // Fetch movie details for resale tickets
//    val moviesToShow = remember(resaleTickets, state.movies) {
//        resaleTickets.mapNotNull { ticket ->
//            // Find the movie corresponding to the ticket by matching the eventId
//            state.movies.find { it.id.toIntOrNull() == ticket.eventId.toInt() }
//           // Log.d("Homescreen", "${ticket.eventId}")
//        }
//    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        item {
            WelcomeSection(
                navController = navController,
                model = userState?.photoUrl ?: DEFAULT_PROFILE_PIC,
                username = userState?.name ?: "Guest"
            )
        }

        stickyHeader {
            SearchBarWithFilter(
                searchQuery = searchQuery,
                onSearchQueryChanged = { query ->
                    viewModel.onSearchQueryChanged(query)
                    searchVisible = true
                },
                onSearch = { viewModel.onSearchQueryChanged(searchQuery) }
            )
        }

        if (searchQuery.isNotEmpty() && searchVisible) {
            if (state.isSearchLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else {
                items(state.searchResults) { item ->
                    SearchResultItem(item = item, onClick = onItemClick)
                }
            }
        }

        item {
            CategorySection(
                categories = categories,
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it }
            )
        }

        if (selectedCategory == "All") {
            item {
                SectionWithSeeAll(
                    title = "Resale Tickets",
                    contentType = "Movies",
                    navController = navController
                ) {
                    if (state.isLoading) {
                        // Placeholder while loading
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
                            CircularProgressIndicator() // Or your custom loading indicator
                        }
                    }
                    else{
                        ResaleTicketCarousel(
                            tickets = resaleTickets,
                            onItemClicked = { ticket ->
                                navController.navigate(Screen.MovieDetailsScreen.createRoute(ticket.eventId))
                            }
                        )
                    }
                }
            }

            item {
                SectionWithSeeAll(
                    title = "Latest Movies",
                    contentType = "Movies",
                    navController = navController
                ) {
                    MoviesSection(
                        state = state,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope,
                        onItemClick = onItemClick
                    )
                }
            }

            item {
                SectionWithSeeAll(
                    title = "Music Concerts",
                    contentType = "Concerts",
                    navController = navController
                ) {
                    HorizontalEventList(events = state.musicEvents)
                }
            }

            item {
                SectionWithSeeAll(
                    title = "Famous Attractions",
                    contentType = "Attractions",
                    navController = navController
                ) {
                    HorizontalAttractionList(attractions = state.attractions)
                }
            }

            item {
                SectionWithSeeAll(
                    title = "Popular Venues",
                    contentType = "Venues",
                    navController = navController
                ) {
                    HorizontalVenueList(venues = state.venues)
                }
            }

            item {
                SectionWithSeeAll(
                    title = "Sports Events",
                    contentType = "Sports",
                    navController = navController
                ) {}
            }

            if (state.isLoading) {
                items(2) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        repeat(2) { ShimmerCard(height = 230.dp, width = 210.dp) }
                    }
                }
            } else {
                gridItems(
                    data = state.sportsEvents,
                    columns = 2
                ) { event ->
                    EventCard(
                        event = event,
                        modifier = Modifier,
                        onClick = { onItemClick(event) }
                    )
                }
            }
        } else {
            // Handle filtered items properly
            val categoryFilteredItems = when (selectedCategory) {
                "Movies" -> state.movies
                "Concerts" -> state.musicEvents
                "Sports" -> state.sportsEvents
                "Venues" -> state.venues
                "Attractions" -> state.attractions
                else -> state.filteredItems.filterIsInstance<Item.Event>()
            }

            if (state.isLoading) {
                items(4) { ShimmerCard(height = 270.dp, width = 210.dp) }
            } else {
                gridItems(
                    data = categoryFilteredItems,
                    columns = 2
                ) { item ->
                    when (item) {
                        is Item.Event -> EventCard(
                            event = item,
                            modifier = Modifier,
                            onClick = { onItemClick(item) }
                        )

                        is Item.Movie -> MovieCard(
                            movie = item,
                            sharedTransitionScope = sharedTransitionScope,
                            animatedVisibilityScope = animatedVisibilityScope,
                            onClick = { navController.navigate("movie_details/${item.id}") }
                        )

                        is Item.Venue -> VenueCard(
                            venue = item,
                            modifier = Modifier,
                            onClick = { /* Handle venue click */ }
                        )

                        is Item.Attraction -> AttractionCard(
                            attraction = item,
                            modifier = Modifier,
                            onClick = { /* Handle attraction click */ }
                        )
                    }
                }
            }
        }
    }
}

fun <T> LazyListScope.gridItems(
    data: List<T>,
    columns: Int,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp),
    itemContent: @Composable (T) -> Unit
) {
    val rows = data.chunked(columns)
    items(rows) { rowItems ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = horizontalArrangement
        ) {
            for (item in rowItems) {
                Box(modifier = Modifier.weight(1f)) {
                    itemContent(item)
                }
            }
            // Fill the remaining space if the last row has fewer items
            if (rowItems.size < columns) {
                for (i in 0 until columns - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class,
    ExperimentalSharedTransitionApi::class)

@Composable
fun CategorySection(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        items(categories) { category ->
            FilterChip(
                border = BorderStroke(color = Color.Transparent, width = 0.dp),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.onPrimary,
                    selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                ),
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                label = { Text(category) }
            )
        }
    }
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MoviesSection(
    state: HomeState,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onItemClick: (Item) -> Unit
) {
    // Collect all items from the ViewModel
    val movies = state.movies

    // Show loading placeholder if no movies are available
    val isLoading = movies.isEmpty()

    if (isLoading) {
        LazyRow {
            items(5) { ShimmerCard(width = 220.dp, height = 300.dp) }
        }
    } else {
        LazyRow {
            items(movies) { movie ->
                MovieCard(
                    movie = movie,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope,
                    onClick = onItemClick
                )
            }
        }
    }
}

@Composable
fun HorizontalEventList(events: List<Item.Event>) {
    val isLoading = events.isEmpty()
    if (isLoading) {
        LazyRow {
            items(5) { ShimmerCard(width = 250.dp, height = 210.dp) }
        }
    } else {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            items(events) { event ->
                EventCard(event = event, Modifier.width(300.dp))
            }
        }
    }
}

@Composable
fun HorizontalVenueList(venues: List<Item.Venue>) {
    val isLoading = venues.isEmpty()
    if (isLoading) {
        LazyRow {
            items(5) { ShimmerCard(width = 200.dp, height = 180.dp) }
        }
    } else {
        LazyRow(
            modifier = Modifier.padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            items(venues) { venue ->
                VenueCard(venue = venue, Modifier.width(200.dp))
            }
        }
    }
}

@Composable
fun HorizontalAttractionList(attractions: List<Item.Attraction>) {
    val isLoading = attractions.isEmpty()
    if (isLoading) {
        LazyRow {
            items(5) { ShimmerCard(width = 270.dp, height = 210.dp) }
        }
    } else {
        LazyRow(
            modifier = Modifier.padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(attractions) { attraction ->
                AttractionCard(attraction = attraction, Modifier.width(270.dp))
            }
        }
    }
}

// -----------------------------------------  ALT TAB LAYOUT DESIGN  -----------------------------------------

//@Composable
//fun HomeScreen(
//    viewModel: HomeViewModel,
//    ticketsViewModel: TicketsViewModel,
//    profileViewModel: ProfileViewModel,
//    sharedTransitionScope: SharedTransitionScope,
//    animatedVisibilityScope: AnimatedVisibilityScope,
//    navController: NavController,
//    onItemClick: (Item) -> Unit
//) {
//    val state by viewModel.state.collectAsState()
//    val userState by profileViewModel.userState.collectAsState()
//    val searchQuery by viewModel.searchQuery.collectAsState()
//    val resaleTickets by ticketsViewModel.resaleTickets.collectAsState()
//
//    var searchVisible by remember { mutableStateOf(true) }
//    val categories = remember { listOf("All", "Sports", "Movies", "Resale", "Concerts") }
//
//    // Create a pager state for tab navigation
//    val pagerState = rememberPagerState(pageCount = {categories.size})
//
//    // Sync the tab selection with the page
//    val coroutineScope = rememberCoroutineScope()
//
//    LaunchedEffect(Unit) {
//        ticketsViewModel.fetchResaleTickets()
//    }
//
//    Column(modifier = Modifier.fillMaxSize()) {
//        // Welcome section and search bar are always visible
//        WelcomeSection(
//            navController = navController,
//            model = userState?.photoUrl ?: DEFAULT_PROFILE_PIC,
//            username = userState?.name ?: "Guest"
//        )
//
//        SearchBarWithFilter(
//            searchQuery = searchQuery,
//            onSearchQueryChanged = rememberUpdatedState<(String) -> Unit> { query ->
//                viewModel.onSearchQueryChanged(query)
//                searchVisible = true
//            }.value,
//            onSearch = { viewModel.onSearchQueryChanged(searchQuery) }
//        )
//
//        // Search results if any (above the tabs)
//        if (searchQuery.isNotEmpty() && searchVisible) {
//            if (state.isLoading) {
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(16.dp),
//                    contentAlignment = Alignment.Center
//                ) {
//                    CircularProgressIndicator()
//                }
//            } else {
//                LazyColumn(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .weight(1f)
//                ) {
//                    items(state.searchResults) { item ->
//                        SearchResultItem(item) { clickedItem ->
//                            if (clickedItem is Item.Movie) {
//                                navController.navigate("movie_details/${clickedItem.id}")
//                            }
//                        }
//                    }
//                }
//            }
//        } else {
//            // Tab layout with category contents
//            TabRow(
//                selectedTabIndex = pagerState.currentPage,
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                categories.forEachIndexed { index, category ->
//                    Tab(
//                        selected = pagerState.currentPage == index,
//                        onClick = {
//                            coroutineScope.launch {
//                                pagerState.animateScrollToPage(index)
//                            }
//                        },
//                        text = { Text(category) }
//                    )
//                }
//            }
//
//            // HorizontalPager for tab content
//            HorizontalPager(
//                state = pagerState,
//                modifier = Modifier.weight(1f)
//            ) { pageIndex ->
//                val category = categories[pageIndex]
//                // Each category gets its own scrollable content
//                CategoryContent(
//                    category = category,
//                    state = state,
//                    viewModel = viewModel,
//                    resaleTickets = resaleTickets,
//                    sharedTransitionScope = sharedTransitionScope,
//                    animatedVisibilityScope = animatedVisibilityScope,
//                    navController = navController
//                )
//            }
//        }
//    }
//}
//
//@OptIn(ExperimentalSharedTransitionApi::class)
//@Composable
//fun CategoryContent(
//    category: String,
//    state: HomeState,
//    viewModel: HomeViewModel,
//    resaleTickets: List<Ticket>,
//    sharedTransitionScope: SharedTransitionScope,
//    animatedVisibilityScope: AnimatedVisibilityScope,
//    navController: NavController
//) {
//    when (category) {
//        "All" -> AllCategoryContent(
//            state = state,
//            viewModel = viewModel,
//            resaleTickets = resaleTickets,
//            sharedTransitionScope = sharedTransitionScope,
//            animatedVisibilityScope = animatedVisibilityScope,
//            navController = navController
//        )
//        "Movies" -> MoviesCategoryContent(
//            movies = state.movies,
//            sharedTransitionScope = sharedTransitionScope,
//            animatedVisibilityScope = animatedVisibilityScope,
//            navController = navController
//        )
//        "Sports" -> SportsCategoryContent(
//            sportsEvents = state.sportsEvents,
//            navController = navController
//        )
//        "Concerts" -> ConcertsCategoryContent(
//            musicEvents = state.musicEvents,
//            navController = navController
//        )
//        "Resale" -> ResaleCategoryContent(
//            viewModel = viewModel,
//            resaleTickets = resaleTickets,
//            state = state,
//            navController = navController
//        )
//    }
//}
//
//@OptIn(ExperimentalSharedTransitionApi::class)
//@Composable
//fun AllCategoryContent(
//    state: HomeState,
//    viewModel: HomeViewModel,
//    resaleTickets: List<Ticket>,
//    sharedTransitionScope: SharedTransitionScope,
//    animatedVisibilityScope: AnimatedVisibilityScope,
//    navController: NavController
//) {
//    LazyColumn(
//        modifier = Modifier.fillMaxSize()
//    ) {
//        item {
//            SectionWithSeeAll("Resale Tickets", "Resale", navController) {
//                ResaleTicketCarousel(
//                    viewModel = viewModel,
//                    tickets = resaleTickets,
//                    onItemClicked = { ticket ->
//                        viewModel.getMovieDetailsById(ticket.eventId, state.movies)?.let { movie ->
//                            navController.navigate(Screen.MovieDetailsScreen.createRoute(movie.id))
//                        }
//                    }
//                )
//            }
//        }
//
//        item {
//            SectionWithSeeAll("Latest Movies", "Movies", navController) {
//                MoviesSection(
//                    state = state,
//                    sharedTransitionScope = sharedTransitionScope,
//                    animatedVisibilityScope = animatedVisibilityScope,
//                    navController = navController
//                )
//            }
//        }
//
//        item {
//            SectionWithSeeAll("Music Concerts", "Concerts", navController) {
//                HorizontalEventList(events = state.musicEvents)
//            }
//        }
//
//        item {
//            SectionWithSeeAll("Popular Venues", "Venues", navController) {
//                HorizontalVenueList(venues = state.venues)
//            }
//        }
//
//        item {
//            SectionWithSeeAll("Famous Attractions", "Attractions", navController) {
//                HorizontalAttractionList(attractions = state.attractions)
//            }
//        }
//    }
//}
//
//@OptIn(ExperimentalSharedTransitionApi::class)
//@Composable
//fun MoviesCategoryContent(
//    movies: List<Item.Movie>,
//    sharedTransitionScope: SharedTransitionScope,
//    animatedVisibilityScope: AnimatedVisibilityScope,
//    navController: NavController
//) {
//    val isLoading = movies.isEmpty()
//
//    if (isLoading) {
//        Box(
//            modifier = Modifier.fillMaxSize(),
//            contentAlignment = Alignment.Center
//        ) {
//            CircularProgressIndicator()
//        }
//    } else {
//        LazyVerticalGrid(
//            columns = GridCells.Fixed(2),
//            contentPadding = PaddingValues(8.dp),
//            verticalArrangement = Arrangement.spacedBy(16.dp),
//            horizontalArrangement = Arrangement.spacedBy(16.dp),
//            modifier = Modifier.fillMaxSize()
//        ) {
//            items(movies) { movie ->
//                MovieGridCard(
//                    movie = movie,
//                    sharedTransitionScope = sharedTransitionScope,
//                    animatedVisibilityScope = animatedVisibilityScope
//                ) {
//                    navController.navigate(Screen.MovieDetailsScreen.createRoute(movie.id))
//                }
//            }
//        }
//    }
//}
//

//@Composable
//fun EventGridCard(
//    event: Item.Event,
//    onClick: () -> Unit
//) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(220.dp)
//            .clickable { onClick() },
//        elevation = CardDefaults.cardElevation(4.dp),
//        shape = RoundedCornerShape(12.dp)
//    ) {
//        Box(modifier = Modifier.fillMaxSize()) {
//            // Event image
//            AsyncImage(
//                model = event.imageUrl,
//                contentDescription = event.imageUrl,
//                contentScale = ContentScale.Crop,
//                modifier = Modifier.fillMaxSize()
//            )
//
//            // Event details overlay
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .align(Alignment.BottomCenter)
//                    .background(
//                        Brush.verticalGradient(
//                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
//                        )
//                    )
//                    .padding(8.dp)
//            ) {
//                Column {
//                    Text(
//                        text = event.name,
//                        style = MaterialTheme.typography.bodyMedium,
//                        color = Color.White,
//                        maxLines = 1,
//                        overflow = TextOverflow.Ellipsis
//                    )
//                    Text(
//                        text = event.date.toString(),
//                        style = MaterialTheme.typography.bodySmall,
//                        color = Color.White.copy(alpha = 0.8f),
//                        maxLines = 1
//                    )
//                }
//            }
//        }
//    }
//}
//

//@OptIn(ExperimentalSharedTransitionApi::class)
//@Composable
//fun MovieGridCard(
//    movie: Item.Movie,
//    sharedTransitionScope: SharedTransitionScope,
//    animatedVisibilityScope: AnimatedVisibilityScope,
//    onClick: () -> Unit
//) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(220.dp)
//            .clickable { onClick() },
//        elevation = CardDefaults.cardElevation(4.dp),
//        shape = RoundedCornerShape(12.dp)
//    ) {
//        Box(modifier = Modifier.fillMaxSize()) {
//            // Image
//            AsyncImage(
//                model = movie.posterPath,
//                contentDescription = movie.title,
//                contentScale = ContentScale.Crop,
//                modifier = Modifier
//                    .fillMaxSize()
////                    .sharedElement(
////                        state = rememberSharedContentState(key = "movie_${movie.id}"),
////                        screenKey = "movie_list",
////                        isFullscreen = false
////                    )
//            )
//
//            // Title overlay at the bottom
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .align(Alignment.BottomCenter)
//                    .background(
//                        Brush.verticalGradient(
//                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
//                        )
//                    )
//                    .padding(8.dp)
//            ) {
//                Text(
//                    text = movie.title,
//                    style = MaterialTheme.typography.bodyMedium,
//                    color = Color.White,
//                    maxLines = 1,
//                    overflow = TextOverflow.Ellipsis
//                )
//            }
//        }
//    }
//}