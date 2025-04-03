package com.example.akirax.data.repository

import android.util.Log
import com.example.akirax.data.api.CashfreeApiService
import com.example.akirax.domain.model.BookingDetails
import com.example.akirax.domain.model.Data
import com.example.akirax.domain.model.PaymentModel
import com.example.akirax.domain.model.PaymentStatusModel
import com.example.akirax.domain.model.Ticket
import com.example.akirax.domain.model.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.util.UUID


class PaymentRepository(
    private val cashfreeApiService: CashfreeApiService,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    suspend fun createOrder(amount: String, customerDetails: UserData): Result<PaymentModel> =
        withContext(Dispatchers.IO) {
            try {
                val response = cashfreeApiService.getOrderID(
                    Data(order_amount = amount.toDouble(), order_currency = "INR", customer_details = customerDetails)
                ).execute()

                if (response.isSuccessful) {
                    val body = response.body()
                    Result.success(body!!)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("PaymentRepository", "Order creation failed: $errorBody")
                    Result.failure(Exception(errorBody))
                }
            } catch (e: Exception) {
                Log.e("PaymentRepository", "Exception during order creation: ${e.message}", e)
                Result.failure(e)
            }
        }

    suspend fun verifyPayment(orderId: String): Result<PaymentStatusModel> = withContext(Dispatchers.IO) {
        try {
            val response = cashfreeApiService.create(orderId).execute()

            if (response.isSuccessful) {
                val body = response.body()
                Result.success(body!!)
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("PaymentRepository", "Payment verification failed: $errorBody")
                Result.failure(Exception(errorBody))
            }
        } catch (e: Exception) {
            Log.e("PaymentRepository", "Exception during payment verification: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun saveTickets(bookingDetails: BookingDetails, userEmail: String): Result<String> = withContext(Dispatchers.IO) {
        val userId = auth.currentUser?.uid ?: return@withContext Result.failure(Exception("User not logged in"))
        val userTicketsRef = firestore.collection("users").document(userId).collection("tickets")
        val tickets = bookingDetails.seats.map { seat ->
            val ticketHash = generateTicketHash("${bookingDetails.eventId}|$seat|$userEmail|${System.currentTimeMillis()}")

            Ticket(
                eventId = bookingDetails.eventId,
                seatNumber = seat,
                ownerEmail = userEmail,
                ticketHash = ticketHash,
                eventName = bookingDetails.title,
                language = bookingDetails.language,
                purchaseTimestamp = System.currentTimeMillis(),
                isForSale = false,
                imageUrl = bookingDetails.posterUrl,
                resalePrice = null
            )
        }

        return@withContext try {
            firestore.runBatch { batch ->
                tickets.forEach { ticket ->
                    val docRef = userTicketsRef.document(ticket.ticketHash)
                    batch.set(docRef, ticket)
                }
            }.await()
            Result.success(tickets.first().ticketHash)
        } catch (e: Exception) {
            Log.e("PaymentRepository", "Error saving tickets to Firebase: ${e.message}", e)
            Result.failure(e)
        }
    }

    private fun generateTicketHash(data: String): String {
        return try {
            MessageDigest.getInstance("SHA-256")
                .digest(data.toByteArray())
                .joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            Log.e("PaymentRepository", "Error generating ticket hash: ${e.message}", e)
            // Fallback to a simple hash if cryptographic hash fails
            "ticket-${UUID.randomUUID()}"
        }
    }
}

