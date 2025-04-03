package com.example.akirax.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.akirax.data.ApiKeys
import com.example.akirax.data.repository.EventRepository
import com.example.akirax.data.repository.MovieRepository
import com.example.akirax.domain.model.Item
import com.example.akirax.domain.model.toDomain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SeeAllViewModel(
    private val eventRepository: EventRepository,
    private val movieRepository: MovieRepository
) : ViewModel() {

    data class SeeAllUiState(
        val isLoading: Boolean = false,
        val events: List<Item.Event> = emptyList(),
        val movies: List<Item.Movie> = emptyList(),
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(SeeAllUiState())
    val uiState: StateFlow<SeeAllUiState> = _uiState

    private var isLoadingMore = false

    private val tmdbApiKey = ApiKeys.tmdbApiKey
    private val ticketmasterApiKey = ApiKeys.ticketmasterApiKey

    fun loadInitial(contentType: String) {
        if (_uiState.value.movies.isNotEmpty() || _uiState.value.events.isNotEmpty()) return
        loadMore(contentType)
    }

    fun loadMore(contentType: String) {
        if (isLoadingMore) return
        isLoadingMore = true

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val newItems = when (contentType) {
                "Movies" -> movieRepository.getPopularMovies(tmdbApiKey).map { it.toDomain() }
                "Sports" -> eventRepository.fetchSportsEvents(ticketmasterApiKey)
                "Music" -> eventRepository.fetchMusicEvents(ticketmasterApiKey)
                else -> emptyList()
            }

            _uiState.update {
                it.copy(
                    movies = if (contentType == "Movies") it.movies + newItems.filterIsInstance<Item.Movie>() else it.movies,
                    events = if (contentType == "Sports" || contentType == "Music") it.events + newItems.filterIsInstance<Item.Event>() else it.events,
                    isLoading = false,
                    error = if (newItems.isEmpty()) "No items found" else null
                )
            }
            isLoadingMore = false
        }
    }
}
