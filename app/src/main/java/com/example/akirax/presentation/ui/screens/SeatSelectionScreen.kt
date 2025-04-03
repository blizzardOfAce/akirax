package com.example.akirax.presentation.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.akirax.R
import com.example.akirax.domain.model.Seat
import com.example.akirax.domain.model.SeatStatus
import com.example.akirax.presentation.ui.components.seatselectionscreen.DateSelectionRow
import com.example.akirax.presentation.ui.components.seatselectionscreen.SelectableSeat
import com.example.akirax.presentation.ui.components.seatselectionscreen.TimeSelectionRow


@Composable
fun SeatSelectionScreen(
    contentPadding: PaddingValues,
    onClickPayment: (String, List<String>, Double) -> Unit,
    movieId: String
) {
    val ticketPrice = 50.0f
    val selectedSeats = remember { mutableStateListOf<Seat>() }

    val calculateTotalPrice: () -> Float = {
        selectedSeats.size * ticketPrice
    }

    SeatSelectionContent(
        onSeatSelected = { seat ->
            if (seat.status == SeatStatus.SELECTED) {
                selectedSeats.add(seat)
            } else {
                selectedSeats.remove(seat)
            }
        },
        selectedSeats = selectedSeats,
        innerPadding = contentPadding,
        calculateTotalPrice = calculateTotalPrice,
        movieId = movieId,
        onClickPayment = onClickPayment
    )
}

@Composable
fun SeatSelectionContent(
    movieId: String,
    onClickPayment: (String, List<String>, Double) -> Unit,
    onSeatSelected: (Seat) -> Unit,
    selectedSeats: List<Seat>,
    innerPadding: PaddingValues,
    calculateTotalPrice: () -> Float
) {
    var selectedDate by remember { mutableIntStateOf(-1) }
    var selectedTime by remember { mutableIntStateOf(-1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                top = innerPadding.calculateTopPadding(),
                end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                bottom = 0.dp // Override bottom padding to 0
            )
            .padding(horizontal = 16.dp)
    ) {
        // Screen header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            val color = MaterialTheme.colorScheme.primary
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(start = 60.dp, end = 60.dp, top = 16.dp)
            ) {
                drawArc(
                    color = color,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(0f, -size.height * 0.5f),
                    size = Size(width = size.width, height = size.height * 2),
                    style = Stroke(width = 18f)
                )
            }
            Text(
                text = "Screen",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Seat Layout
        val seatRows = listOf("A" to 5, "B" to 7, "C" to 8, "D" to 8, "E" to 8, "F" to 7, "G" to 5)
        val seats = remember { mutableStateListOf<Seat>() }

        seatRows.forEach { (rowLabel, seatCount) ->
            repeat(seatCount) { seatNumber ->
                val status = when ((0..2).random()) {
                    0 -> SeatStatus.BOOKED
                    else -> SeatStatus.AVAILABLE
                }
                seats.add(Seat(rowLabel, seatNumber + 1, status))
            }
        }

        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            items(seatRows) { (rowLabel, seatCount) ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    repeat(seatCount) { seatNumber ->
                        val seat = seats.first { it.row == rowLabel && it.number == seatNumber + 1 }
                        SelectableSeat(seat = seat, onClick = { onSeatSelected(seat) })
                    }
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.seat_car_svgrepo_com_2_),
                    contentDescription = "selected seat",
                    modifier = Modifier.size(30.dp),
                    tint = Color.Unspecified
                )

                Text("Selected")
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.seat_car_svgrepo_com_2_),
                    contentDescription = "available seat",
                    modifier = Modifier.size(30.dp),
                    tint = Color.LightGray
                )
                Text("Available")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.seat_car_svgrepo_com_2_),
                    contentDescription = "booked seat",
                    modifier = Modifier.size(30.dp),
                    tint = Color.DarkGray
                )
                Text("Booked")
            }

        }

        Spacer(modifier = Modifier.weight(1f))

        // Bottom UI with date, time, and confirm button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.DarkGray, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
        ) {
            Column {
                Text(
                    text = "Select Date & Time",
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    fontSize = 22.sp,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                DateSelectionRow(selectedDate, onDateSelected = { selectedDate = it })
                Spacer(modifier = Modifier.height(12.dp))
                TimeSelectionRow(selectedTime, onTimeSelected = { selectedTime = it })

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Total Price: ", color = Color.White, fontSize = 20.sp)
                        Text(
                            "${calculateTotalPrice()}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                    Button(
                        modifier = Modifier.height(56.dp),
                        enabled = selectedSeats.isNotEmpty() && selectedDate != -1 && selectedTime != -1,
                        onClick = {
                            val selectedSeatNumbers = selectedSeats.map { "${it.row}${it.number}" }
                            val totalPrice = calculateTotalPrice().toDouble()

                            // Pass only necessary data instead of full object
//                            val args = listOf(
//                                "movieId=${Uri.encode(movieId)}",
//                                "seats=${Uri.encode(selectedSeatNumbers.joinToString(","))}",
//                                "totalPrice=${totalPrice}"
//                            ).joinToString("&")

                            onClickPayment(movieId, selectedSeatNumbers, totalPrice)
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Confirm Seat", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
