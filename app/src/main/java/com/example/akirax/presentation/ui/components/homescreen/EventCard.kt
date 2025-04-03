package com.example.akirax.presentation.ui.components.homescreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.akirax.domain.model.Item

@Composable
fun EventCard(
    event: Item.Event,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .padding(4.dp)
            .clickable(onClick = onClick)
    ) {
        Box(modifier = Modifier.aspectRatio(4f/3f)) {
            AsyncImage(
                model = event.imageUrl,
                contentDescription = event.name,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            // Optional: Add a like/favorite button
            IconButton(
                onClick = { /* Favorite logic */ },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    //.padding(4.dp)
                //    .background(Color.White.copy(alpha = 0.3f), shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = Color.White
                )
            }
        }

        Text(
            text = event.name,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            ),
            modifier = Modifier.padding(top = 8.dp, start = 4.dp)
        )
    }
}

//@Composable
//fun EventCard(event: Item.Event, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
//    Card(
//        modifier = modifier
//            .padding(4.dp)
//            //.clip(RoundedCornerShape(8.dp))
//            .clickable(onClick = onClick),
//        //elevation = CardDefaults.cardElevation(4.dp)
//        colors = CardDefaults.cardColors(Color.Transparent)
//    ) {
//        Column(modifier = Modifier.padding(4.dp)) {
//            AsyncImage(
//                model = event.imageUrl,
//                contentDescription = event.name,
//                modifier = Modifier
//                    .border(
//                        width = 0.5.dp,
//                        color = Color.DarkGray,
//                        shape = RoundedCornerShape(8.dp)
//                    )
//                    .fillMaxWidth()
//                    .clip(RoundedCornerShape(8.dp))
//                    .height(210.dp),
//                contentScale = ContentScale.Crop
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//            Text(
//                event.name,
//                style = MaterialTheme.typography.bodyMedium,
//                maxLines = 1,
//                lineHeight = 22.sp,
//                overflow = TextOverflow.Ellipsis
//            )
//        }
//    }
//}