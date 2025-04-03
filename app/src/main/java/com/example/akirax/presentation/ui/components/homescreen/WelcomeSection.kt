package com.example.akirax.presentation.ui.components.homescreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.akirax.Screen
import com.example.akirax.presentation.ui.theme.Poppins

@Composable
fun WelcomeSection(
    navController: NavController,
    model: String,
    username: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Welcome, $username 👋",
                fontWeight = FontWeight.SemiBold,
               // fontFamily = Poppins,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Book your favourite event",
                fontSize = 14.sp,
               // fontFamily = Poppins
            )
        }
        IconButton(
            onClick = { navController.navigate(Screen.ProfileScreen.route) },
            modifier = Modifier.size(40.dp)
        ) {
            AsyncImage(
                model = model,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .clip(CircleShape)
                    .size(40.dp),
                contentScale = ContentScale.Crop
            )
        }
    }
}