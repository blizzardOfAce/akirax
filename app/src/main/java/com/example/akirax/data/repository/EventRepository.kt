package com.example.akirax.data.repository

import TicketmasterApiService
import android.util.Log
import com.example.akirax.domain.model.Item

class EventRepository(private val apiService: TicketmasterApiService) {

    suspend fun fetchMusicEvents(apiKey: String): List<Item.Event> {
        val response = apiService.getMusicEvents(apiKey)
        return if (response.isSuccessful) {
            response.body()?._embedded?.events?.map {
                Item.Event(
                    id = it.id,
                    name = it.name,
                    description = it.description ?: "No description available",
                    date = it.dates.start.localDate,
                    venueId = it._embedded?.venues?.firstOrNull()?.name,
                    imageUrl = it.images?.firstOrNull()?.url
                )
            } ?: emptyList()
        } else {
            Log.e("EventRepository", "Error fetching music events: ${response.errorBody()?.string()}")
            emptyList()
        }
    }

    suspend fun fetchSportsEvents(apiKey: String): List<Item.Event> {
        val response = apiService.getSportsEvents(apiKey)
        return if (response.isSuccessful) {
            response.body()?._embedded?.events?.map {
                Item.Event(
                    id = it.id,
                    name = it.name,
                    description = it.description ?: "No description available",
                    date = it.dates.start.localDate,
                    venueId = it._embedded?.venues?.firstOrNull()?.name,
                    imageUrl = it.images?.firstOrNull()?.url
                )
            } ?: emptyList()
        } else {
            Log.e("EventRepository", "Error fetching sports events: ${response.errorBody()?.string()}")
            emptyList()
        }
    }

    suspend fun fetchNearbyVenues(apiKey: String): List<Item.Venue> {
        // Call the API with optional keyword and geoPoint
        val response = apiService.getNearbyVenues(apiKey, "us")

        if (!response.isSuccessful) {
            Log.e("EventRepository", "API Error: ${response.code()}, ${response.errorBody()?.string()}")
            return emptyList()
        }

        // Get the body and check for venues in _embedded
        val body = response.body()
        val venues = body?.embedded?.venues
        if (venues == null) {
            Log.e("EventRepository", "No venues found in response")
            return emptyList()
        }
        // Map and return venue data
        return venues.map { venue ->
            Item.Venue(
                id = venue.id,
                name = venue.name,
                address = venue.location?.latitude ?: "Unknown address",
                city = venue.city?.name ?: "Unknown city",
                country = venue.country?.name ?: "Unknown country",
                imageUrl = venue.images?.firstOrNull()?.url
            )
        }
    }

    suspend fun fetchPopularAttractions(apiKey: String): List<Item.Attraction> {
        val response = apiService.getAttractions(apiKey)
        return if (response.isSuccessful) {
            response.body()?.embedded?.attractions?.map {
                Item.Attraction(
                    id = it.id,
                    name = it.name,
                    type = it.type,
                    imageUrl = it.images?.firstOrNull()?.url
                )
            } ?: emptyList()
        } else {
            Log.e("EventRepository", "Error fetching popular attractions: ${response.errorBody()?.string()}")
            emptyList()
        }
    }
}
