package com.example.akirax.domain.model

data class Ticket(
    val eventId: String = "",
    val eventName: String = "",
    val language: String = "",
    val seatNumber: String = "",
    val ownerEmail: String = "",
    val ticketHash: String = "",
    val isForSale: Boolean = false,
    val resalePrice: Double? = null,
    val imageUrl: String? = null,
    val purchaseTimestamp: Long = 0L
) {
    // No-argument constructor will be generated with default values
}

