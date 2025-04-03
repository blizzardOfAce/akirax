package com.example.akirax.domain.model

import Image
import com.google.gson.annotations.SerializedName

data class VenueResponse(
    @SerializedName("_embedded") val embedded: EmbeddedVenues?
) {
    data class EmbeddedVenues(
        val venues: List<Venue>?
    )
}

data class Venue(
    val id: String,
    val name: String,
    val country: Country?,
    val city: City?,
    val location: Location?,
    val images: List<Image>?
)

data class Country(
    val name: String
)

data class City(
    val name: String
)

data class Location(
    val latitude: String,
    val longitude: String
)

data class AttractionResponse(
    @SerializedName("_embedded") val embedded: EmbeddedVenues?
) {
    data class EmbeddedVenues(
        val attractions: List<Attraction>?
    )
}

data class Attraction(
    val id: String,
    val name: String,
    val type: String,
    val images: List<Image>?
)
