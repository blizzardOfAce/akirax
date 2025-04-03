package com.example.akirax.presentation.ui.components.moviedetailsscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// InfoCard Composable
@Composable
fun InfoCard(icon: ImageVector, item: String, info: String) {
    Card(
        modifier = Modifier,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(6.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally, // Align items to the start
            verticalArrangement = Arrangement.Center // Align items vertically in the center
        ) {
            //icon() // Use the icon as a Composable
            Icon(imageVector = icon, contentDescription = item, tint = MaterialTheme.colorScheme.primary)
            Text(text = item, fontWeight = FontWeight.Light, fontSize = 14.sp)
            Text(text = info, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}
