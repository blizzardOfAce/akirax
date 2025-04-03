package com.example.akirax.domain.model

enum class SeatStatus {
    AVAILABLE, BOOKED, SELECTED
}

data class Seat(val row: String, val number: Int, var status: SeatStatus)

