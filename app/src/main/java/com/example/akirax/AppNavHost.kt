package com.example.akirax

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.akirax.domain.model.Item
import com.example.akirax.domain.model.ItemType
import com.example.akirax.domain.model.getItemType
import com.example.akirax.presentation.ui.screens.ETicketScreen
import com.example.akirax.presentation.ui.screens.EditProfileScreen
import com.example.akirax.presentation.ui.screens.EventDetailsScreen
import com.example.akirax.presentation.ui.screens.HomeScreen
import com.example.akirax.presentation.ui.screens.LoginScreen
import com.example.akirax.presentation.ui.screens.MovieDetailsScreen
import com.example.akirax.presentation.ui.screens.PaymentScreen
import com.example.akirax.presentation.ui.screens.ProfileScreen
import com.example.akirax.presentation.ui.screens.SearchScreen
import com.example.akirax.presentation.ui.screens.SeatSelectionScreen
import com.example.akirax.presentation.ui.screens.SeeAllScreen
import com.example.akirax.presentation.ui.screens.SignUpScreen
import com.example.akirax.presentation.ui.screens.StartScreen
import com.example.akirax.presentation.ui.screens.TicketsScreen
import com.example.akirax.presentation.viewmodel.AuthViewModel
import com.example.akirax.presentation.viewmodel.HomeViewModel
import com.example.akirax.presentation.viewmodel.ProfileViewModel
import com.example.akirax.presentation.viewmodel.TicketsViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AppNavHost(
    navController: NavHostController,
    viewModel: AuthViewModel = koinViewModel(),
    innerPaddingValues: PaddingValues,
) {
    val authState by viewModel.authState.collectAsState()
    val homeViewModel: HomeViewModel = koinViewModel()
    val profileViewModel: ProfileViewModel = koinViewModel()
    val ticketsViewModel: TicketsViewModel = koinViewModel()

    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = if (authState.isAuthenticated) Screen.HomeScreen.route else Screen.StartScreen.route,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left, tween(300)
                ) + fadeIn(tween(300))
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left, tween(300)
                ) + fadeOut(tween(300))
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right, tween(300)
                ) + fadeIn(tween(300))
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right, tween(300)
                ) + fadeOut(tween(300))
            }) {
            composable(Screen.StartScreen.route) {
                StartScreen(onClickLogin = {
                    navController.navigate(Screen.LoginScreen.route)
                }, onClickSignUp = {
                    navController.navigate(Screen.SignUpScreen.route)
                })
            }

            composable(Screen.HomeScreen.route) {
                HomeScreen(
                    viewModel = homeViewModel,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this,
                    profileViewModel = profileViewModel,
                    ticketsViewModel = ticketsViewModel,
                    navController = navController
                ) { item ->
                    when (getItemType(item)) {
                        ItemType.MOVIE -> {
                            val movie = item as? Item.Movie
                            navController.navigate(
                                Screen.MovieDetailsScreen.createRoute(
                                    movie?.id ?: "NULL"
                                )
                            )
                        }

                        ItemType.EVENT -> {
                            val event = item as? Item.Event
                            //Later
                        }

                        else -> {}
                    }
                }
            }

            composable(Screen.LoginScreen.route, enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Up, tween(300)
                ) + fadeIn(tween(300))
            }, exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Up, tween(300)
                ) + fadeOut(tween(300))
            }, popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Down, tween(300)
                ) + fadeIn(tween(300))
            }, popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Down, tween(300)
                ) + fadeOut(tween(300))
            }) {
                LoginScreen(
                    onClickRegister = {
                        navController.navigate(Screen.SignUpScreen.route)
                        {
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                            }
                        }
                    },
                    onClickLogin = {
                        navController.navigate(Screen.HomeScreen.route) {
                            popUpTo(Screen.LoginScreen.route)
                        }
                    })
            }

            composable(Screen.SignUpScreen.route) {
                SignUpScreen(
                    onClickLogin = {
                        navController.navigate(Screen.LoginScreen.route)
                        {
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                            }
                        }
                    })
            }

            composable(
                route = Screen.MovieDetailsScreen.route,
                arguments = listOf(navArgument("movieId") { type = NavType.StringType })
            ) { backStackEntry ->
                val movieId = backStackEntry.arguments?.getString("movieId") ?: ""
                MovieDetailsScreen(
                    movieId = movieId,
                    homeViewModel = homeViewModel,
                    contentPadding = innerPaddingValues,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this,
                    ticketsViewModel = ticketsViewModel,
                    onClickSelectSeat = {
                        navController.navigate(Screen.SeatSelectionScreen.createRoute(movieId))
                    })
            }

            composable(
                route = Screen.EventDetailsScreen.route,
                arguments = listOf(navArgument("eventId") { type = NavType.StringType })
            ) { backStackEntry ->
                val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
                EventDetailsScreen(eventId)
            }

            composable(
                route = Screen.SeatSelectionScreen.route,
                arguments = listOf(navArgument("movieId") { type = NavType.StringType })
            ) { backStackEntry ->
                val movieId = backStackEntry.arguments?.getString("movieId") ?: ""
                SeatSelectionScreen(
                    onClickPayment = { movieId, selectedSeats, totalPrice ->
                        navController.navigate(
                            Screen.PaymentScreen.createRoute(
                                movieId, selectedSeats, totalPrice
                            )
                        )
                    }, contentPadding = innerPaddingValues, movieId = movieId
                )
            }

            composable(Screen.TicketsScreen.route) {
                TicketsScreen(
                    innerPadding = innerPaddingValues, onClickViewTicket = { ticketHash ->
                        navController.navigate(Screen.ETicketScreen.createRoute(ticketHash))
                    },
                    viewModel = ticketsViewModel,
                    onClickTicket = { eventId ->
                        navController.navigate(Screen.MovieDetailsScreen.createRoute(eventId))
                    }
                )
            }

            composable(
                route = Screen.PaymentScreen.route,
                arguments = listOf(
                    navArgument("movieId") { type = NavType.StringType },
                    navArgument("seats") { type = NavType.StringType },
                    navArgument("totalPrice") { type = NavType.StringType })
            ) { backStackEntry ->
                val movieId = backStackEntry.arguments?.getString("movieId") ?: ""
                val seats = backStackEntry.arguments?.getString("seats")?.split(",") ?: emptyList()
                val totalPrice =
                    backStackEntry.arguments?.getString("totalPrice")?.toDoubleOrNull() ?: 0.0
                val ticketHash = ticketsViewModel.ticketHash.collectAsState().value

                PaymentScreen(
                    onGoHome = { navController.navigate(Screen.HomeScreen.route) },
                    onViewTicket = {
                        navController.navigate(
                            Screen.ETicketScreen.createRoute(
                                ticketHash ?: ""
                            )
                        )
                    },
                    paddingValues = innerPaddingValues,
                    homeViewModel = homeViewModel,
                    movieId = movieId,
                    seats = seats,
                    ticketsViewModel = ticketsViewModel,
                    totalPrice = totalPrice
                )
            }

            composable(
                route = Screen.ETicketScreen.route,
                arguments = listOf(navArgument("ticketHash") { type = NavType.StringType })
            ) { backStackEntry ->
                val ticketHash =
                    backStackEntry.arguments?.getString("ticketHash") ?: return@composable
                ETicketScreen(
                    ticketHash = ticketHash, ticketsViewModel = ticketsViewModel
                )
            }

            composable(Screen.SearchScreen.route) {
                SearchScreen(
                    innerPadding = innerPaddingValues,
                ) { item ->
                    when (getItemType(item)) {
                        ItemType.MOVIE -> {
                            val movie = item as? Item.Movie
                            movie?.let {
                                navController.navigate(Screen.MovieDetailsScreen.createRoute(movie.id))
                            }
                        }

                        ItemType.EVENT -> {
                            val event = item as? Item.Event
                            event?.let {}
                        }

                        else -> {}
                    }
                }
            }

            composable("seeAll/{contentType}") { backStackEntry ->
                val contentType =
                    backStackEntry.arguments?.getString("contentType") ?: return@composable
                SeeAllScreen(
                    contentType = contentType,
                    contentPadding = innerPaddingValues,
                    onClickMovieCard = { movieId ->
                        navController.navigate(Screen.MovieDetailsScreen.createRoute(movieId))
                    })
            }

            composable(Screen.ProfileScreen.route) {
                ProfileScreen(
                    profileViewModel = profileViewModel,
                    innerPadding = innerPaddingValues,
                    onClickEditProfile = { navController.navigate(Screen.EditProfileScreen.route) },
                    onClickTicket = { navController.navigate(Screen.TicketsScreen.route) },
                    onLogoutClick = {
                        viewModel.logout()
                        navController.navigate(Screen.LoginScreen.route)
                        profileViewModel.clearData()
                    })
            }

            composable(Screen.EditProfileScreen.route) {
                EditProfileScreen(
                    innerPadding = innerPaddingValues, onClick = {
                        navController.popBackStack()
                    })
            }
        }
    }
}

sealed class Screen(val route: String) {
    object TicketsScreen : Screen("tickets")
    object ETicketScreen : Screen("eticket/{ticketHash}") {
        fun createRoute(ticketHash: String) = "eticket/$ticketHash"
    }

    object EditProfileScreen : Screen("EditProfileScreen")
    object EventDetailsScreen : Screen("EventDetailsScreen/{eventId}") {
        fun createRoute(eventId: String) = "EventDetailsScreen/$eventId"
    }

    object HomeScreen : Screen("HomeScreen")
    object LoginScreen : Screen("LoginScreen")
    object MovieDetailsScreen : Screen("MovieDetailsScreen/{movieId}") {
        fun createRoute(movieId: String) = "MovieDetailsScreen/$movieId"
    }

    object PaymentScreen :
        Screen("payment_screen?movieId={movieId}&seats={seats}&totalPrice={totalPrice}") {
        fun createRoute(movieId: String, seats: List<String>, totalPrice: Double): String {
            val seatsParam = seats.joinToString(",")
            return "payment_screen?movieId=$movieId&seats=$seatsParam&totalPrice=$totalPrice"
        }
    }

    object ProfileScreen : Screen("ProfileScreen")
    object SearchScreen : Screen("SearchScreen")
    object SeatSelectionScreen : Screen("SeatSelectionScreen/{movieId}") {
        fun createRoute(movieId: String) = "SeatSelectionScreen/$movieId"
    }

    object SeeAllScreen : Screen("SeeAllScreen/{contentType}")
    object SignUpScreen : Screen("SignUpScreen")
    object StartScreen : Screen("StartScreen")
}






