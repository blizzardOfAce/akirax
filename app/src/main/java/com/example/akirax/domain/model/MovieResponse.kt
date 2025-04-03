package com.example.akirax.domain.model

import com.google.gson.annotations.SerializedName

data class Genre(
    val id: Int,
    val name: String
)

data class MovieResponse(
    val results: List<Movie>,
    val genres: List<Genre>? = null
)

data class Movie(
    val id: Int,
    val title: String,
    @SerializedName("release_date") val releaseDate: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("overview") val overview: String,
    val genres: List<Genre> = emptyList(),
    val runtime: Int? = null,
    @SerializedName("original_language") val originalLanguage: String?, // Ensure name matches API
    @SerializedName("vote_average") val voteAverage: Float? = null,
    val cast: List<String>? = emptyList()
)

