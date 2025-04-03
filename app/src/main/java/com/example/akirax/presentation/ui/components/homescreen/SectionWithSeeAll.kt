package com.example.akirax.presentation.ui.components.homescreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.akirax.presentation.ui.theme.Poppins

@Composable
fun SectionWithSeeAll(
    title: String,
    contentType: String,
    navController: NavController,
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = Color.LightGray,
            fontWeight = FontWeight.SemiBold,
            fontFamily = Poppins,
            fontSize = 18.sp
        )
        IconButton(onClick = {
            // Navigate to SeeAllScreen with the contentType as an argument
            navController.navigate("seeAll/$contentType")
        }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "more",
                modifier = Modifier.size(12.dp)
            )
        }
    }
    content()
}