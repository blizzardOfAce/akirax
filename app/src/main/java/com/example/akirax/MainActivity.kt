package com.example.akirax

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.akirax.presentation.ui.components.BottomNavBar
import com.example.akirax.presentation.ui.components.TopBar
import com.example.akirax.presentation.ui.theme.AkiraXTheme
import com.example.akirax.presentation.viewmodel.AuthViewModel
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModel()
    private var showSplashScreen = true

    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen().apply {
            setKeepOnScreenCondition {
                showSplashScreen
            }
            setOnExitAnimationListener { screen ->
                val zoomX = ObjectAnimator.ofFloat(
                    screen.iconView, View.SCALE_X,
                    0.35f, 0f
                )
                val zoomY = ObjectAnimator.ofFloat(
                    screen.iconView, View.SCALE_Y,
                    0.35f, 0f
                )
                zoomX.duration = 500
                zoomY.duration = 500
                zoomX.interpolator = LinearInterpolator()
                zoomY.interpolator = LinearInterpolator()
                zoomX.doOnEnd {
                    screen.remove()
                }
                zoomY.doOnEnd {
                    screen.remove()
                }
                zoomY.start()
                zoomX.start()
            }
        }
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()


        fun getScreenTitle(route: String?): String {
            return when (route) {
                Screen.SearchScreen.route -> "Search Movies"
                Screen.TicketsScreen.route -> "My Tickets"
                Screen.ProfileScreen.route -> "Profile"
                Screen.MovieDetailsScreen.route -> "Details"
                Screen.PaymentScreen.route -> "Payment"
                Screen.SeeAllScreen.route -> ""
                Screen.EventDetailsScreen.route -> ""
                Screen.SeatSelectionScreen.route -> "Select Seat"
                Screen.EditProfileScreen.route -> "Edit Profile"
                else -> "AkiraX"
            }
        }
        setContent {
            AkiraXTheme {
                val navController = rememberNavController()
                val currentBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = currentBackStackEntry?.destination?.route

                val bottomNavScreens = listOf(
                    Screen.HomeScreen.route,
                    Screen.SearchScreen.route,
                    Screen.TicketsScreen.route,
                    Screen.ProfileScreen.route
                )

                val hideTopBarScreens = listOf(
                    Screen.HomeScreen.route,
                    Screen.ETicketScreen.route,
                    Screen.StartScreen.route,
                    Screen.LoginScreen.route,
                    Screen.SignUpScreen.route
                )

                Scaffold(
                    topBar = {
                        if (currentRoute !in hideTopBarScreens) {
                            TopBar(
                                title = getScreenTitle(currentRoute),
                                onBackPressed =
                                    { navController.popBackStack() }

                            )
                        }
                    },
                    bottomBar = {
                        AnimatedVisibility(
                            visible = currentRoute in bottomNavScreens && currentRoute != Screen.ProfileScreen.route,
                            enter = slideInVertically(
                                initialOffsetY = { it }, // Starts off-screen at the bottom
                                animationSpec = tween(
                                    durationMillis = 300,
                                    easing = FastOutSlowInEasing
                                )
                            ) + fadeIn(),
                            exit = slideOutVertically(
                                targetOffsetY = { it }, // Moves out to the bottom
                                animationSpec = tween(
                                    durationMillis = 300,
                                    easing = FastOutSlowInEasing
                                )
                            ) + fadeOut()
                        ) {
                            BottomNavBar(
                                selectedIndex = bottomNavScreens.indexOf(currentRoute),
                                onItemSelected = { index ->
                                    navController.navigate(bottomNavScreens[index]) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    AppNavHost(
                        navController = navController,
                        viewModel = authViewModel,
                        innerPaddingValues = innerPadding
                    )
                }
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            delay(1000)
            showSplashScreen = false
        }
    }
}
