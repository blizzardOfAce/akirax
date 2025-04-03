package com.example.akirax.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MovieSummary(
    val movieId: String,
    val title: String,
    val language: String,
    val rating: Float,
    val duration: String,
    val posterUrl: String
) : Parcelable

@Parcelize
data class BookingDetails(
    val eventId: String,
    val language: String,
    val posterUrl: String,
    val title: String,
    val rating: Float,
    val duration: String,
    val seats: List<String>,
    val totalPrice: Double
) : Parcelable


