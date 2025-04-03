package com.example.akirax.presentation.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.akirax.R
import com.example.akirax.domain.model.Item
import com.example.akirax.presentation.ui.components.homescreen.SearchResultItem
import com.example.akirax.presentation.viewmodel.HomeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchScreen(
    innerPadding: PaddingValues,
  //  navController: NavController,
    homeViewModel: HomeViewModel = koinViewModel(), // Get the HomeViewModel
    onItemClick: (Item) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val searchResults = remember { mutableStateListOf<Item.Movie>() }
    val isLoading = remember { mutableStateOf(false) }
    val searchCache = remember { mutableMapOf<String, List<Item.Movie>>() }


    // Fetch movies when the search query changes
    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotEmpty()) {
            isLoading.value = true
            searchResults.clear()

            // Check cache first
            if (searchCache.containsKey(searchQuery)) {
                searchResults.addAll(searchCache[searchQuery]!!)
                isLoading.value = false
            } else {
                try {
                    val movies = homeViewModel.fetchFromTmdb(searchQuery)
                    searchCache[searchQuery] = movies
                    searchResults.addAll(movies)
                } catch (e: Exception) {
                    Log.e("SearchScreen", "Error fetching movies: ${e.message}")
                } finally {
                    isLoading.value = false
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .padding(innerPadding)
    ) {

        OutlinedTextField(
            value = searchQuery,
            maxLines = 1,
            onValueChange = { searchQuery = it },
            label = { Text("Search for events") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Loading Indicator
        if (isLoading.value) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 16.dp)
            )
        } else {
            if (searchQuery.isEmpty()) {
                Spacer(modifier = Modifier.height(64.dp))
                Image(
                    painter = painterResource(R.drawable.search_placeholder),
                    contentDescription = "search image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(420.dp)
                )
            }
            // Display Search Results
            else {
                LazyColumn {
                    items(searchResults) { movie ->
                        SearchResultItem(item = movie, onClick = { onItemClick(movie) })
                    }
                }
            }
        }
    }
}
