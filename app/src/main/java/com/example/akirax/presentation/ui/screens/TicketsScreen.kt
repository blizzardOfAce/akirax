package com.example.akirax.presentation.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.akirax.domain.model.Ticket
import com.example.akirax.presentation.viewmodel.TicketsViewModel
import kotlinx.coroutines.launch

@Composable
fun TicketsScreen(
    innerPadding: PaddingValues,
    onClickTicket: (String) -> Unit,
    onClickViewTicket: (String) -> Unit,
    viewModel: TicketsViewModel
) {
    val ticketList by viewModel.tickets.collectAsState(emptyList())
    val resaleTicketList by viewModel.resaleTickets.collectAsState(emptyList())
    val selectedTab = remember { mutableStateOf("Upcoming") }
    val tabs = listOf("Upcoming", "Resale", "Cancelled")
    val coroutineScope = rememberCoroutineScope()


    var hasFetchedOnce by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (resaleTicketList.isEmpty() && !hasFetchedOnce) {
            hasFetchedOnce = true
            viewModel.fetchResaleTickets()
        }
    }

    Column(
        modifier = Modifier.padding(innerPadding)
    ) {
        // Tabs for filtering tickets
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            tabs.forEach { tab ->
                Text(
                    text = tab,
                    modifier = Modifier
                        .padding(start = 12.dp, end = 12.dp, bottom = 12.dp)
                        .clickable { selectedTab.value = tab },
                    color = if (selectedTab.value == tab) MaterialTheme.colorScheme.primary else Color.Gray,
                    fontWeight = if (selectedTab.value == tab) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
        HorizontalDivider()

        // Display filtered ticket list
        LazyColumn(modifier = Modifier.padding(8.dp)) {
            items(
                when (selectedTab.value) {
                "Resale" -> resaleTicketList
                "Upcoming" -> ticketList
                "Cancelled" -> ticketList.filter { ticket ->
                    ticket.isForSale
                }

                else -> ticketList
            }) { ticket ->
                TicketItem(
                    ticket = ticket, onClickCancelTicket = {
                        coroutineScope.launch {
                            viewModel.cancelBooking(
                                ticket = ticket
                            )
                            viewModel.fetchAllTickets()
                        }
                    },
                    onClickViewTicket = { onClickViewTicket(ticket.ticketHash) },
                    onClickTicket = { onClickTicket(ticket.eventId) }
                )
            }
        }
    }
}

@Composable
fun TicketItem(
    ticket: Ticket,
    onClickTicket: () -> Unit,
    onClickCancelTicket: () -> Unit,
    onClickViewTicket: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable {
                onClickTicket()
            }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                ticket.eventName,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground

            )
            Text("Language: ${ticket.language}", color =  MaterialTheme.colorScheme.onBackground, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    onClick = onClickCancelTicket
                ) {
                    Text(text = "Cancel Booking", color = MaterialTheme.colorScheme.onBackground)
                }

                Button(
                    onClick = onClickViewTicket,
                ) {
                    Text(text = "View Ticket", color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}

