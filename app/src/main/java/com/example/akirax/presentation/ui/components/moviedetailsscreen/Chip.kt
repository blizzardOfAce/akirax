package com.example.akirax.presentation.ui.components.moviedetailsscreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Chip Composable for Cast Members
@Composable
fun Chip(text: String) {
    Card(
        shape = RoundedCornerShape(25),
        modifier = Modifier.padding(6.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.bodyMedium,

            )
    }
}