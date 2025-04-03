package com.example.akirax.presentation.ui.screens

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.akirax.PaymentVerificationActivity
import com.example.akirax.domain.model.BookingDetails
import com.example.akirax.domain.model.UserData
import com.example.akirax.domain.model.formatRating
import com.example.akirax.presentation.viewmodel.PaymentViewModel
import org.koin.androidx.compose.koinViewModel
import com.example.akirax.presentation.viewmodel.HomeViewModel
import com.example.akirax.presentation.viewmodel.TicketsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    onViewTicket: () -> Unit,
    onGoHome: () -> Unit,
    homeViewModel: HomeViewModel,
    ticketsViewModel: TicketsViewModel,
    paddingValues: PaddingValues,
    movieId: String,
    seats: List<String>,
    totalPrice: Double,
    viewModel: PaymentViewModel = koinViewModel(viewModelStoreOwner = LocalContext.current as ComponentActivity),
) {
    val movieDetails = homeViewModel.findMovieDetailsById(movieId)
    val bookingDetails = BookingDetails(
        eventId = movieId,
        language = movieDetails?.originalLanguage ?: "N/A",
        posterUrl = movieDetails?.posterPath ?: "",
        title = movieDetails?.title ?: "Not Available",
        rating = movieDetails?.voteAverage ?: 0f,
        duration = movieDetails?.runtime ?: "N/A",
        seats = seats,
        totalPrice = totalPrice
    )

    val orderId by viewModel.orderId.collectAsState()
    val paymentSessionId by viewModel.paymentSessionId.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val paymentStatus by viewModel.paymentStatus.collectAsState()

    var showSuccessBanner by remember { mutableStateOf(false) }
    var showFailureBanner by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->

        when (result.resultCode) {
            Activity.RESULT_OK -> {
                val paymentStatus = result.data?.getStringExtra("PAYMENT_STATUS")
                val orderID = result.data?.getStringExtra("ORDER_ID")

                if (paymentStatus == "VERIFIED" && orderID != null) {
                    viewModel.verifyPayment(orderID)
                } else {
                    Toast.makeText(context, "Payment verification failed", Toast.LENGTH_SHORT).show()
                }
            }

            Activity.RESULT_CANCELED -> {
                val paymentStatus = result.data?.getStringExtra("PAYMENT_STATUS")
                val errorMessage = result.data?.getStringExtra("ERROR_MESSAGE")

                when (paymentStatus) {
                    "CANCELLED" -> {
                        Toast.makeText(context, "Payment Cancelled", Toast.LENGTH_SHORT).show()
                        viewModel.resetState()
                    }
                    "FAILED" -> {
                        Toast.makeText(
                            context,
                            "Payment Failed: ${errorMessage ?: "Unknown error"}",
                            Toast.LENGTH_SHORT
                        ).show()
                        viewModel.resetState()
                    }
                    else -> {
                        Toast.makeText(context, "Payment Interrupted", Toast.LENGTH_SHORT).show()
                        viewModel.resetState()
                    }
                }
            }
        }
    }

    // Initialize payment data when the screen first loads
    LaunchedEffect(Unit) {
        viewModel.setPaymentData(
            details = bookingDetails,
            email = "MFDOOM@supervillain.com"
        )
    }

    LaunchedEffect(paymentStatus) {
        if (paymentStatus == "Paid") {
            showSuccessBanner = true
        } else if (paymentStatus == "Failed") {
            showFailureBanner = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            MovieHeader(movieDetails = bookingDetails)
            Spacer(modifier = Modifier.weight(0.80f))
            Text(
                text = "Seats: ${bookingDetails.seats.joinToString(", ")}",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp
            )
            Text(
                text = "Total Price: ₹${bookingDetails.totalPrice}",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (!showSuccessBanner && !showFailureBanner) {
                Button(
                    onClick = {
                        val customerDetails = UserData(
                            customer_id = "DOOM",
                            customer_name = "Daniel Dumile",
                            customer_email = "rhymeslikedimes@hairline.com",
                            customer_phone = "+911234567890"
                        )

                        // Start payment flow or launch existing payment session
                        if (orderId == null || paymentSessionId == null) {
                            viewModel.startPaymentFlow(context, totalPrice.toString(), customerDetails)
                        } else {
                            val intent = Intent(context, PaymentVerificationActivity::class.java).apply {
                                putExtra("ORDER_ID", orderId)
                                putExtra("PAYMENT_SESSION_ID", paymentSessionId)
                            }
                            launcher.launch(intent)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    } else {
                        Text("Proceed to Payment")
                    }
                }
            }
        }

        // Payment Result Banners
        if (showSuccessBanner || showFailureBanner) {
            PaymentResultBanner(
                isSuccess = showSuccessBanner,
                onViewTicket = onViewTicket,
                onGoHome = onGoHome,
                onRetryPayment = {
                    showFailureBanner = false
                    viewModel.resetState()

                    val customerDetails = UserData(
                        customer_id = "vaudeville_villain",
                        customer_name = "Victor Vaughn",
                        customer_email = "caniw@tch.com",
                        customer_phone = "+911234567890"
                    )
                    viewModel.startPaymentFlow(
                        context = context,
                        amount = bookingDetails.totalPrice.toString(),
                        customerDetails = customerDetails
                    )
                }
            )
            LaunchedEffect(Unit) {
                ticketsViewModel.fetchTicketHashByEventId(bookingDetails.eventId)
                ticketsViewModel.fetchAllTickets()
            }
        }
    }
}

@Composable
fun PaymentResultBanner(
    isSuccess: Boolean,
    onViewTicket: () -> Unit,
    onGoHome: () -> Unit,
    onRetryPayment: () -> Unit
) {
    val title = if (isSuccess) "Congratulations!" else "Payment Failed!"
    val message = if (isSuccess) {
        "Ticket booked successfully.\nEnjoy the movie!"
    } else {
        "Something went wrong while processing your payment.\nPlease try again!"
    }
    val buttonText = if (isSuccess) "View E-Ticket" else "Retry Payment"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)) // Blurred background effect
            .clickable { } // Makes background non-clickable
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors().copy(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.7f),
                contentColor = Color.White),
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
                .align(Alignment.Center) // This centers the card inside the Box
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth()
            ) {
                Icon(
                    imageVector = if (isSuccess) Icons.Filled.CheckCircle else Icons.Filled.Error,
                    contentDescription = "Result Icon",
                    tint = if (isSuccess) Color(0xFF2ECC71) else Color(0xFFE74C3C),
                    modifier = Modifier.size(72.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    onClick = onViewTicket,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(buttonText, color = MaterialTheme.colorScheme.onPrimary)
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    onClick = onGoHome
                ) {
                    Text("Go to Home")
                }
                if (!isSuccess) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = onRetryPayment
                    ) {
                        Text("Retry Payment")
                    }
                }
            }
        }
    }
}


@Composable
fun MovieHeader(movieDetails: BookingDetails) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(0.5.dp, Color.LightGray, RoundedCornerShape(12.dp))
            .background(
                color = Color.LightGray.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            )
    )
    {
        Row(modifier = Modifier.padding(4.dp)) {
            Image(
                painter = rememberAsyncImagePainter(model = "https://image.tmdb.org/t/p/w500${movieDetails.posterUrl}"),
                contentDescription = "Movie Poster",
                modifier = Modifier
                    .size(150.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(verticalArrangement = Arrangement.Center) {
                Text(text = movieDetails.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = "Duration: ${movieDetails.duration}", color = MaterialTheme.colorScheme.onBackground)
                Row {
                    Text(text = "Rating: ", color = MaterialTheme.colorScheme.onBackground)
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "rating",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(text = " ${formatRating(movieDetails.rating)}", color = MaterialTheme.colorScheme.onBackground)
                }

            }
        }
    }
}


