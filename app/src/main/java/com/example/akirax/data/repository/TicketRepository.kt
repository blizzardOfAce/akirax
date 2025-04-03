package com.example.akirax.data.repository

import android.util.Log
import com.example.akirax.domain.model.Ticket
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

interface TicketsRepository {
    suspend fun fetchTicketHashByEventId(userId: String, eventId: String): String?
    suspend fun fetchTicket(userId: String, ticketHash: String): Ticket?
    suspend fun fetchAllTickets(userId: String): List<Ticket>
    suspend fun fetchResaleTickets(): List<Ticket>
    suspend fun markTicketForResale(userId: String, ticket: Ticket, resalePrice: Double)
    suspend fun cancelBooking(userId: String, ticket: Ticket)
    suspend fun cancelResale(userId: String, ticketHash: String)
}

class FirebaseTicketsRepository : TicketsRepository {
    private val db = FirebaseFirestore.getInstance()

override suspend fun fetchTicketHashByEventId(userId: String, eventId: String): String? {
    return try {
        val querySnapshot = db.collection("users").document(userId)
            .collection("tickets").whereEqualTo("eventId", eventId)
            .get().await()
        if (querySnapshot.isEmpty) {
            null
        } else {
            val ticketHash = querySnapshot.documents.first().toObject(Ticket::class.java)?.ticketHash
            ticketHash
        }
    } catch (e: Exception) {
        Log.e("TicketsRepository", "Error fetching ticket hash for eventId: $eventId, userId: $userId, error: ${e.message}")
        null
    }
}

    override suspend fun fetchTicket(userId: String, ticketHash: String): Ticket? {
        return try {
            val document = db.collection("users")
                .document(userId)
                .collection("tickets")
                .document(ticketHash)
                .get()
                .await()
            val ticket = document.toObject(Ticket::class.java)
            ticket
        } catch (e: Exception) {
            Log.e("TicketsRepository", "Error fetching ticket for ticketHash: $ticketHash, userId: $userId, error: ${e.message}")
            null
        }
    }

    override suspend fun fetchAllTickets(userId: String): List<Ticket> {
        return try {
            val querySnapshot = db.collection("users")
                .document(userId)
                .collection("tickets")
                .get()
                .await()
            val tickets = querySnapshot.documents.mapNotNull { it.toObject(Ticket::class.java) }
            tickets
        } catch (e: Exception) {
            Log.e("TicketsRepository", "Error fetching all tickets for userId: $userId, error: ${e.message}")
            emptyList()
        }
    }

    override suspend fun fetchResaleTickets(): List<Ticket> {
        return try {
            val querySnapshot = db.collection("resaleTickets")
                .get()
                .await()
            val resaleTickets = querySnapshot.documents.mapNotNull { it.toObject(Ticket::class.java) }
            resaleTickets
        } catch (e: Exception) {
            Log.e("TicketsRepository", "Error fetching resale tickets: ${e.message}")
            emptyList()
        }
    }

    override suspend fun markTicketForResale(userId: String, ticket: Ticket, resalePrice: Double) {
        try {
            val resaleTicket = ticket.copy(
                isForSale = true,
                resalePrice = resalePrice
            )

            db.collection("resaleTickets").document(ticket.ticketHash)
                .set(resaleTicket).await()

            db.collection("users").document(userId)
                .collection("tickets").document(ticket.ticketHash)
                .update(
                    mapOf(
                        "isForSale" to true,
                        "resalePrice" to resalePrice
                    )
                ).await()

        } catch (e: Exception) {
            Log.e("TicketsRepository", "Error listing ticket for resale: ${e.message}", e)
        }
    }

    override suspend fun cancelBooking(userId: String, ticket: Ticket) {
        try {
            db.collection("users").document(userId).collection("tickets").document(ticket.ticketHash).delete().await()
        } catch (e: Exception) {
            Log.e("TicketsRepository", "Error canceling ticket: ${e.message}")
        }
    }

    override suspend fun cancelResale(userId: String, ticketHash: String) {
        try {
            val userTicketRef = db.collection("users").document(userId).collection("tickets").document(ticketHash)
            val resaleTicketRef = db.collection("resaleTickets").document(ticketHash)

            db.runTransaction { transaction ->
                val userTicketSnapshot = transaction.get(userTicketRef)
                if (userTicketSnapshot.exists()) {
                    transaction.update(userTicketRef, mapOf("isForSale" to false, "resalePrice" to null))
                    transaction.delete(resaleTicketRef)
                } else {
                    throw IllegalStateException("Ticket not found in user's collection.")
                }
            }.await()
        } catch (e: Exception) {
            Log.e("TicketsRepository", "Error canceling resale: ${e.message}")
        }
    }
}
