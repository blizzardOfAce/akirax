package com.example.akirax.utils

import com.example.akirax.domain.model.Ticket
import com.google.firebase.firestore.FirebaseFirestore
import java.security.MessageDigest

fun generateTicketHash(data: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
    return digest.digest(data.toByteArray()).joinToString("") { "%02x".format(it) }
}

fun generateTicket(
    eventId: String,
    eventName: String,
    language: String,
    seatNumber: String,
    ownerEmail: String
): Ticket {
    val ticketHash = generateTicketHash("$eventId|$eventName|$language|$seatNumber|$ownerEmail")
    return Ticket(
        eventId = eventId,
        eventName = eventName,
        language = language,
        seatNumber = seatNumber,
        ownerEmail = ownerEmail,
        ticketHash = ticketHash,
        purchaseTimestamp = System.currentTimeMillis(),
        isForSale = false,
        resalePrice = null
    )
}

fun updateTicketStatus(
    ticketId: String,
    updates: Map<String, Any>,
    onCompletion: (Boolean, String) -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    db.collection("tickets").document(ticketId)
        .update(updates)
        .addOnSuccessListener { onCompletion(true, "Update successful.") }
        .addOnFailureListener { e -> onCompletion(false, "Error: ${e.localizedMessage}") }
}

fun transferTicketOwnership(ticketId: String, newOwnerEmail: String, onCompletion: (Boolean, String) -> Unit) {
    updateTicketStatus(
        ticketId,
        mapOf("ownerEmail" to newOwnerEmail),
        onCompletion
    )
}

fun markTicketForResale(ticketId: String, resalePrice: Double, onCompletion: (Boolean, String) -> Unit) {
    updateTicketStatus(
        ticketId,
        mapOf("isForSale" to true, "resalePrice" to resalePrice),
        onCompletion
    )
}



