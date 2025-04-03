package com.example.akirax.data.repository

import android.util.Log
import com.example.akirax.data.api.TMDBApiService
import com.example.akirax.domain.model.Movie

class MovieRepository(private val api: TMDBApiService) {

    suspend fun getPopularMovies(apiKey: String): List<Movie> {
        return runCatching {
            val response = api.getPopularMovies(apiKey)
            response.results
        }.onFailure {
            Log.e("MovieRepository", "Error fetching popular movies: ${it.message}")
        }.getOrDefault(emptyList())
    }


    suspend fun getUpcomingMovies(apiKey: String): List<Movie> {
        return runCatching { api.getUpcomingMovies(apiKey).results }
            .onFailure { Log.e("MovieRepository", "Error fetching upcoming movies: ${it.message}") }
            .getOrDefault(emptyList())
    }

    suspend fun searchMovies(apiKey: String, query: String): List<Movie> {
        return runCatching { api.searchMovies(apiKey, query).results }
            .onFailure { Log.e("MovieRepository", "Error searching movies: ${it.message}") }
            .getOrDefault(emptyList())
    }

    suspend fun fetchMovieDetails(apiKey: String, movieId: Int): Movie {
        return runCatching {
            val movie = api.getMovieDetails(movieId, apiKey)
            val credits = api.getMovieCredits(movieId, apiKey)

            Movie(
                id = movie.id,
                title = movie.title,
                overview = movie.overview,
                genres = movie.genres ?: emptyList(),
                runtime = movie.runtime ?: 0,
                posterPath = movie.posterPath,
                originalLanguage = movie.originalLanguage.orEmpty(),
                voteAverage = movie.voteAverage ?: 0f,
                releaseDate = movie.releaseDate.orEmpty(),
                cast = credits.cast.take(5).mapNotNull { it.name }
            )
        }.onFailure {
            Log.e("MovieRepository", "Error fetching details for movie $movieId: ${it.message}")
        }.getOrThrow()
    }
}


