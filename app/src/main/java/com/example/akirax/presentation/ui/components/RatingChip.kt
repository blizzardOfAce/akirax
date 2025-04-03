package com.example.akirax.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RatingChip(modifier: Modifier, rating:String){
    Box(
        modifier = modifier
            .padding(12.dp)
            .border(
                width = 0.5.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(45)
            )
            .background(
                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.7f),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(45)
            )
    ) {
        Row(
            modifier = Modifier.Companion.padding(vertical = 2.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.Companion.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Icon(
                modifier = Modifier.Companion.size(18.dp),
                imageVector = Icons.Default.Star,
                contentDescription = "Rating",
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = rating,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 12.sp
                )
            )
        }
    }
}