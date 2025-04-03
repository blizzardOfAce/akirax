package com.example.akirax.presentation.ui.components.seatselectionscreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.akirax.R
import com.example.akirax.domain.model.Seat
import com.example.akirax.domain.model.SeatStatus

@Composable
fun SelectableSeat(seat: Seat, onClick: (Seat) -> Unit) {
    var isSelected by remember { mutableStateOf(seat.status == SeatStatus.SELECTED) }

    val seatColor = when (seat.status) {
        SeatStatus.BOOKED -> Color.Companion.DarkGray
        SeatStatus.AVAILABLE -> if (isSelected) Color.Companion.Unspecified else Color.Companion.LightGray
        SeatStatus.SELECTED -> Color.Companion.Unspecified// Highlight selected seats
    }

    // Disable interaction if the seat is booked
    val modifier = Modifier.Companion
        .size(40.dp)
        .clickable(enabled = seat.status != SeatStatus.BOOKED) {
            if (seat.status == SeatStatus.AVAILABLE) {
                isSelected = !isSelected
                seat.status = if (isSelected) SeatStatus.SELECTED else SeatStatus.AVAILABLE
                onClick(seat)
            }
        }

    // Seat Icon with the appropriate color
    Icon(
        painter = painterResource(id = R.drawable.seat_car_svgrepo_com_2_), // Replace with actual seat drawable
        contentDescription = "Seat ${seat.row}${seat.number}",
        modifier = modifier,
        tint = seatColor
    )
}