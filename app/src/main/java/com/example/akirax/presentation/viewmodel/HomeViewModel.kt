package com.example.akirax.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.akirax.data.ApiKeys
import com.example.akirax.data.repository.EventRepository
import com.example.akirax.data.repository.MovieRepository
import com.example.akirax.domain.model.Item
import com.example.akirax.domain.model.toDomain
import com.example.akirax.utils.HomeState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class HomeViewModel(
    private val eventRepository: EventRepository,
    private val movieRepository: MovieRepository
) : ViewModel() {

    private val tmdbApiKey = ApiKeys.tmdbApiKey
    private val ticketmasterApiKey = ApiKeys.ticketmasterApiKey

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(500)
                .filter { it.isNotBlank() }
                .collectLatest { query ->
                    performSearch(query)
                }
        }
    }

    internal fun loadContent() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                val results = coroutineScope {
                    listOf(
                        async { fetchUniqueMusicEvents() },
                        async { fetchUniqueSportsEvents() },
                        async { movieRepository.getUpcomingMovies(tmdbApiKey).map { it.toDomain() } }, // Convert to Item.Movie
                        async { fetchDistinctNearbyVenues() },
                        async { fetchDistinctPopularAttractions() }
                    ).map { it.await() }
                }

                val music = results.getOrNull(0) ?: emptyList()
                val sports = results.getOrNull(1) ?: emptyList()
                val movies = (results.getOrNull(2) as? List<*>)?.filterIsInstance<Item.Movie>() ?: emptyList() // Proper filtering
                val venues = results.getOrNull(3) ?: emptyList()
                val attractions = results.getOrNull(4) ?: emptyList()

                _state.update {
                    it.copy(
                        movies = movies,
                        musicEvents = music.filterIsInstance<Item.Event>(),
                        sportsEvents = sports.filterIsInstance<Item.Event>(),
                        venues = venues.filterIsInstance<Item.Venue>()
                            .filter { venue -> !venue.imageUrl.isNullOrEmpty() },
                        attractions = attractions.filterIsInstance<Item.Attraction>(),
                        isLoading = false
                    )
                }

                // Fetch detailed movie data in background
                movies.forEach { movie ->
                    fetchAndUpdateMovieDetails(movie.id)
                }

            } catch (e: Exception) {
                Log.e("HomeViewModel", "Failed to load content: ${e.message}", e)
                _state.update { it.copy(error = "Failed to load content: ${e.message}", isLoading = false) }
            }
        }

    }

    private fun fetchAndUpdateMovieDetails(movieId: String) {
        viewModelScope.launch {
            runCatching {
                movieRepository.fetchMovieDetails(tmdbApiKey, movieId.toInt()).toDomain()
            }.onSuccess { details ->
                _state.update { currentState ->
                    currentState.copy(
                        movies = currentState.movies.map { movie ->
                            if (movie.id == movieId) movie.copy(
                                overview = details.overview,
                                releaseDate = details.releaseDate,
                                genres = details.genres,
                                runtime = details.runtime
                            ) else movie
                        }
                    )
                }
            }.onFailure { e ->
                Log.e("HomeViewModel", "Failed to fetch movie details: ${e.message}", e)
            }
        }
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            _state.update { it.copy(isSearchLoading = true) }
            try {
                val (tmdbResults, ticketmasterResults) = coroutineScope {
                    val tmdbDeferred = async { fetchFromTmdb(query) }
                    val ticketmasterDeferred = async { fetchFromTicketmaster(query) }
                    tmdbDeferred.await() to ticketmasterDeferred.await()
                }

                _state.update { it.copy(searchResults = tmdbResults + ticketmasterResults) }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Search failed: ${e.message}", e)
                _state.update { it.copy(error = "Search failed: ${e.message}") }
            } finally {
                _state.update { it.copy(isSearchLoading = false) }
            }
        }
    }

    internal suspend fun fetchFromTmdb(query: String): List<Item.Movie> {
        return try {
            val result = movieRepository.searchMovies(tmdbApiKey, query)
                .map { it.toDomain() }
            result
        } catch (e: Exception) {
            Log.e("HomeViewModel", "Failed to fetch movies from TMDB: ${e.message}", e)
            emptyList()
        }
    }

    private suspend fun fetchFromTicketmaster(query: String): List<Item.Event> {
        return try {
            val result = (eventRepository.fetchMusicEvents(ticketmasterApiKey) +
                    eventRepository.fetchSportsEvents(ticketmasterApiKey))
                .filter { it.name.contains(query, ignoreCase = true) }
            result
        } catch (e: Exception) {
            Log.e("HomeViewModel", "Failed to fetch events from Ticketmaster: ${e.message}", e)
            emptyList()
        }
    }

    fun getMovieDetails(movieId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val details = movieRepository.fetchMovieDetails(tmdbApiKey, movieId).toDomain()

                _state.update { currentState ->
                    val updatedMovies = currentState.movies.map { movie ->
                        if (movie.id.toIntOrNull() == movieId) details else movie
                    }.toMutableList()

                    // If the movie wasn't in the list, add it
                    if (updatedMovies.none { it.id.toIntOrNull() == movieId }) {
                        updatedMovies.add(details)
                    }
                    currentState.copy(movies = updatedMovies)
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Failed to fetch movie details: ${e.message}", e)
                _state.update { it.copy(error = "Failed to fetch movie details") }
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun filterItemsByCategory(category: String) {
        _state.update {
            it.copy(
                filteredItems = when (category) {
                    "All" -> _state.value.items
                    "Sports" -> _state.value.items.filterIsInstance<Item.Event>()
                        .filter { it.category == "Sports" }

                    "Movies" -> _state.value.items.filterIsInstance<Item.Movie>()
                    "Concerts" -> _state.value.items.filterIsInstance<Item.Event>()
                        .filter { it.category == "Concerts" }

                    else -> emptyList()
                }
            )
        }
    }

    private suspend fun fetchUniqueMusicEvents(): List<Item.Event> {
        return try {
            eventRepository.fetchMusicEvents(ticketmasterApiKey)
                .distinctBy { it.name.lowercase() }
        } catch (e: Exception) {
            Log.e("HomeViewModel", "Failed to fetch music events: ${e.message}", e)
            emptyList()
        }
    }

    private suspend fun fetchUniqueSportsEvents(): List<Item.Event> {
        val result = eventRepository.fetchSportsEvents(ticketmasterApiKey)
            .distinctBy { it.name to it.date }
        return result
    }

    private suspend fun fetchDistinctNearbyVenues(): List<Item.Venue> {
        val result = eventRepository.fetchNearbyVenues(ticketmasterApiKey)
            .distinctBy { it.id }
        return result
    }

    private suspend fun fetchDistinctPopularAttractions(): List<Item.Attraction> {
        val result = eventRepository.fetchPopularAttractions(ticketmasterApiKey)
            .distinctBy { it.id }
        return result
    }

    fun findMovieDetailsById(eventId: String): Item.Movie? {
        return state.value.movies.find { it.id == eventId }
    }
}
