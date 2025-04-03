package com.example.akirax.presentation.ui.components.seatselectionscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DateSelectionRow(
    selectedDateIndex: Int,
    onDateSelected: (Int) -> Unit
) {
    val dayFormatter = remember { DateTimeFormatter.ofPattern("EEE") }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("d") }
    val dates = remember { (0 until 10).map { LocalDate.now().plusDays(it.toLong()) } }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.Companion.padding(horizontal = 16.dp)
    ) {
        items(dates.size) { index ->
            val date = dates[index]
            val isSelected = index == selectedDateIndex

            Column(
                horizontalAlignment = Alignment.Companion.CenterHorizontally,
                modifier = Modifier.Companion
                    .width(72.dp)
                    // .size(56.dp) // Ensuring equal chip size
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Companion.LightGray
                    )
                    .clickable { onDateSelected(index) }
                    .padding(12.dp)
            ) {
                Text(
                    text = date.format(dayFormatter), // Display day (e.g., "Mon")
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Companion.Black
                )
                Spacer(modifier = Modifier.Companion.height(4.dp))
                Text(
                    text = date.format(dateFormatter), // Display date (e.g., "5")
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Companion.SemiBold,
                    color = Color.Companion.Black
                )
            }
        }
    }
}