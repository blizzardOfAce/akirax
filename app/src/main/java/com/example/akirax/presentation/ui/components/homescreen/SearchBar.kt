package com.example.akirax.presentation.ui.components.homescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.akirax.R
import com.example.akirax.domain.model.Item
import com.example.akirax.domain.model.formatRating


@Composable
fun SearchBarWithFilter(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onSearch: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp) // Consistent spacing
    ) {
        // Search Bar
        BasicTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            singleLine = true,
            cursorBrush = SolidColor(Color.Gray),
            textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onBackground),
            modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .border(0.3.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp),
            decorationBox = { innerTextField ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        if (searchQuery.isEmpty()) Text("Search", color = Color.Gray)
                        innerTextField()
                    }
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChanged("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.Gray)
                        }
                    }
                }
            }
        )

        // Filter Button
        IconButton(
            onClick = onSearch,
            modifier = Modifier.size(36.dp) // Adjust button size
        ) {
            Icon(
                painter = painterResource(R.drawable.tune_36dp),
                contentDescription = "Filter",
                tint = Color.Gray,
                modifier = Modifier.size(36.dp) // Adjust icon size within the button
            )
        }
    }
}

@Composable
fun SearchResultItem(item: Item, onClick: (Item) -> Unit) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .clickable(onClick = { onClick(item) })
            .background(
                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                RoundedCornerShape(8.dp)
            )
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Check type and display different data based on type
            when (item) {
                is Item.Movie -> {
                    // Display movie-related data
                    AsyncImage(
                        model = "https://image.tmdb.org/t/p/w500${item.posterPath}",
                        contentDescription = "Movie Poster",
                        modifier = Modifier.size(64.dp)
                    )
                    Column(modifier = Modifier.padding(4.dp)) {
                        Text(text = item.title, style = MaterialTheme.typography.bodySmall)
                        Row(
                            modifier = Modifier.padding(2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Rating: ${formatRating(item.voteAverage)}")
                            Text(text = " Language: ${item.originalLanguage} ")
                        }
                    }
                }
                is Item.Event -> {
                    // Display event-related data
                    AsyncImage(
                        model = item.imageUrl,
                        contentDescription = "Event Image",
                        modifier = Modifier.size(64.dp)
                    )
                    Column(modifier = Modifier.padding(4.dp)) {
                        Text(text = item.name, style = MaterialTheme.typography.bodySmall)
                        Text(text = item.category)
                        Text(text = item.date!!)
                    }
                }
                else -> {}
            }
        }
    }
}