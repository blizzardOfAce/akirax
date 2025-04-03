package com.example.akirax.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.akirax.Screen

@Composable
fun BottomNavBar(selectedIndex: Int, onItemSelected: (Int) -> Unit) {

    // Navigation items
    val items = listOf(
        Screen.HomeScreen.route to Icons.Default.Home,
        Screen.SearchScreen.route to Icons.Default.Search,
        Screen.TicketsScreen.route to Icons.Default.ConfirmationNumber,
        Screen.ProfileScreen.route to Icons.Default.Person
    )

    Box(
        modifier = Modifier.Companion
            //.border(1.dp, color = Color.White, RoundedCornerShape(16.dp))
            // It adds weird border at the bottom inset thing
            //.padding(horizontal = 44.dp, vertical = 16.dp)
            .padding(start = 44.dp, end = 44.dp, top = 16.dp, bottom = 48.dp)
            //.windowInsetsBottomHeight(WindowInsets.systemBars)
            //.consumeWindowInsets(WindowInsets.systemBars)
            .fillMaxWidth()
            // .offset(y = (-32).dp)
            // .height(64.dp)
            .heightIn(min = 0.dp, max = 56.dp)
//            .shadow(
//                elevation = 20.dp,
//                shape = RoundedCornerShape(16.dp),
//                ambientColor = Yellow40.copy(alpha = 0.9f), // Glow effect color
//                spotColor = Yellow80.copy(alpha = 0.9f)
//            )
            .background(
                brush = Brush.Companion.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary
                        //Yellow80.copy(alpha = 0.9f), // Gradient with transparency
                        //Yellow40.copy(alpha = 0.9f)
                    )
                ),

                shape = RoundedCornerShape(16.dp)
            ),
        contentAlignment = Alignment.Companion.Center
    ) {
        Row(
            modifier = Modifier.Companion
//                .shadow(
//                    elevation = 15.dp,
//                    shape = RoundedCornerShape(16.dp),
//                    ambientColor = Yellow40.copy(alpha = 0.9f), // Glow effect color
//                    spotColor = Yellow80.copy(alpha = 0.9f)
//                )
                .border(
                    1.dp,
                    color = MaterialTheme.colorScheme.tertiary,
                    androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                )
                .fillMaxSize()
                .padding(vertical = 8.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.Companion.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            items.forEachIndexed { index, item ->
                IconButton(
                    onClick = { onItemSelected(index) },
                    modifier = Modifier.Companion.size(36.dp)
                ) {
                    Icon(
                        imageVector = item.second,
                        contentDescription = item.first,
                        modifier = Modifier.Companion.size(36.dp),
                        tint = if (selectedIndex == index) Color.Companion.White else Color.Companion.Gray
                    )
                }
            }
        }
    }
}