package com.example.akirax.presentation.ui.screens

import com.example.akirax.presentation.viewmodel.TicketsViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.getViewModel

@Composable
fun ETicketScreen(
    ticketHash: String,
    ticketsViewModel: TicketsViewModel = getViewModel()
) {
    val ticket by ticketsViewModel.ticket.collectAsState()
    val qrBitmap by ticketsViewModel.qrCode.collectAsState()

    // Fetch ticket details
    LaunchedEffect(ticketHash) {
        ticketsViewModel.fetchTicket(ticketHash = ticketHash)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 64.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        Text(
            text = "Your e-Ticket",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Display QR Code
        qrBitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "QR Code",
                modifier = Modifier
                    .size(320.dp)
                    .padding(8.dp)
            )
        } ?: CircularProgressIndicator(modifier = Modifier
            .size(48.dp)
            .fillMaxWidth())

        Spacer(modifier = Modifier.height(16.dp))

        // Display ticket details
        ticket?.let {
           Column{
                Text("Event: ${it.eventName}")
                Text("Language: ${it.language}")
                Text("Seat: ${it.seatNumber}")
            }
        } ?: Text("No ticket information available")
    }
}



