package com.example.akirax.utils

import com.example.akirax.domain.model.Item

data class HomeState(
    val items: List<Item> = emptyList(),
    val filteredItems: List<Item> = emptyList(),
    val searchResults: List<Item> = emptyList(),
    val seeAllItems: List<Item> = emptyList(),
    val movies: List<Item.Movie> = emptyList(),
    val musicEvents: List<Item.Event> = emptyList(),
    val sportsEvents: List<Item.Event> = emptyList(),
    val venues: List<Item.Venue> = emptyList(),
    val attractions: List<Item.Attraction> = emptyList(),
    val isLoading: Boolean = false,
    val isSearchLoading:Boolean = false,
    val error: String? = null
)
