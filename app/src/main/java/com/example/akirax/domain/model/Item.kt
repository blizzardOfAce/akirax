package com.example.akirax.domain.model

sealed class Item {
    data class Movie(
        val id: String,
        val title: String,
        val overview: String?,
        val genres: List<String>?,
        val runtime: String?,
        val posterPath: String?,
        val originalLanguage: String?,
        val voteAverage: Float?,
        val releaseDate: String?,
        val cast: List<String>
    ): Item()

    data class Event(
        val id: String,
        val name: String,
        val description: String,
        val date: String?,
        val venueId: String?,
        val imageUrl: String?,
        val category: String = "" // Added to help with filtering
    ): Item()

    data class Venue(
        val id: String,
        val name: String,
        val address: String,
        val city: String,
        val country: String,
        val imageUrl: String?
    ) : Item()

    data class Attraction(
        val id: String,
        val name: String,
        val type: String,
        val imageUrl: String?
    ) : Item()
}

fun Movie.toDomain() = Item.Movie(
        id = this.id.toString(),
        title = this.title,
        overview = this.overview,
        genres = this.genres?.map { it.name } ?: emptyList(), // Ensure list is not null
        runtime = formatRuntime(this.runtime), // Ensure runtime formatting is correct
        posterPath = this.posterPath,
        originalLanguage = this.originalLanguage,
        voteAverage = this.voteAverage ?: 0f,
        releaseDate = this.releaseDate,
        cast = this.cast ?: emptyList() // Ensure cast is not null
    )

// Enum for Item Types
enum class ItemType {
    MOVIE,
    EVENT,
    VENUE,
    ATTRACTION
}

// Function to determine ItemType
fun getItemType(item: Item): ItemType {
    return when (item) {
        is Item.Movie -> ItemType.MOVIE
        is Item.Event -> ItemType.EVENT
        is Item.Venue -> ItemType.VENUE
        is Item.Attraction -> ItemType.ATTRACTION
    }
}

fun formatRuntime(minutes: Int?): String {
    if (minutes == null || minutes <= 0) return "Unknown"
    val hours = minutes / 60
    val remainingMinutes = minutes % 60
    return "${hours}h ${remainingMinutes}min"
}

fun formatRating(rating: Float?): String {
    return rating?.let { String.format("%.1f", it) } ?: "N/A"
}