package com.example.akirax.presentation.ui.components.seatselectionscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun TimeSelectionRow(
    selectedTimeIndex: Int,
    onTimeSelected: (Int) -> Unit
) {
    val times = listOf("9:00", "12:00", "15:00", "18:00")

    LazyRow(
        horizontalArrangement = Arrangement.SpaceEvenly,
        //.spacedBy(8.dp),
        modifier = Modifier.Companion.fillMaxWidth().padding(horizontal = 16.dp)
    ) {
        items(times.size) { index ->
            val time = times[index]
            val isSelected = index == selectedTimeIndex

            Box(
                contentAlignment = Alignment.Companion.Center,
                modifier = Modifier.Companion
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Companion.LightGray
                    )
                    .clickable { onTimeSelected(index) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = time,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Companion.SemiBold,
                    color = Color.Companion.Black
                )
            }
        }
    }
}