package com.example.akirax.presentation.viewmodel

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.akirax.PaymentVerificationActivity
import com.example.akirax.data.repository.PaymentRepository
import com.example.akirax.domain.model.BookingDetails
import com.example.akirax.domain.model.UserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/* Todo: Update the ticket booking logic for same movie */

class PaymentViewModel(private val repository: PaymentRepository) : ViewModel() {

    private val _paymentStatus = MutableStateFlow<String?>(null)
    val paymentStatus: StateFlow<String?> = _paymentStatus

    private val _orderId = MutableStateFlow<String?>(null)
    val orderId: StateFlow<String?> = _orderId

    private val _paymentSessionId = MutableStateFlow<String?>(null)
    val paymentSessionId: StateFlow<String?> = _paymentSessionId

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _ticketHash = MutableStateFlow<String?>(null)
    val ticketHash: StateFlow<String?> = _ticketHash

    var bookingDetails: BookingDetails? = null
    var userEmail: String? = null

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun setPaymentData(details: BookingDetails, email: String) {
        bookingDetails = details
        userEmail = email
    }

    fun startPaymentFlow(context: Context, amount: String, customerDetails: UserData) {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val result = repository.createOrder(amount, customerDetails)
                if (result.isSuccess) {
                    result.getOrNull()?.let { paymentModel ->
                        _orderId.value = paymentModel.orderId
                        _paymentSessionId.value = paymentModel.paymentSessionId

                        val intent =
                            Intent(context, PaymentVerificationActivity::class.java).apply {
                                putExtra("ORDER_ID", paymentModel.orderId)
                                putExtra("PAYMENT_SESSION_ID", paymentModel.paymentSessionId)
                            }
                        context.startActivity(intent)
                    } ?: run {
                        Log.e("PaymentViewModel", "Payment order creation returned null")
                        _paymentStatus.value = "Failed"
                        _error.value = "Failed to create payment order"
                    }
                } else {
                    val exception = result.exceptionOrNull()
                    Log.e(
                        "PaymentViewModel",
                        "Payment order creation failed: ${exception?.message}"
                    )
                    _paymentStatus.value = "Failed"
                    _error.value =
                        "Failed to create payment order: ${exception?.message ?: "Unknown error"}"
                }
            } catch (e: Exception) {
                Log.e("PaymentViewModel", "Exception during payment flow start: ${e.message}", e)
                _paymentStatus.value = "Failed"
                _error.value = "Payment initialization error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun verifyPayment(orderId: String) {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val result = repository.verifyPayment(orderId)
                if (result.isSuccess) {
                    val statusModel = result.getOrNull()

                    if (statusModel?.order_status == "PAID") {
                        _paymentStatus.value = "Paid"
                        // Immediately save tickets after successful payment
                        saveTicketsToFirebase()
                    } else {
                        _paymentStatus.value = "Failed"
                        _error.value =
                            "Payment not completed: ${statusModel?.order_status ?: "Unknown status"}"
                    }
                } else {
                    val exception = result.exceptionOrNull()
                    Log.e("PaymentViewModel", "Payment verification failed: ${exception?.message}")
                    _paymentStatus.value = "Failed"
                    _error.value =
                        "Payment verification failed: ${exception?.message ?: "Unknown error"}"
                }
            } catch (e: Exception) {
                Log.e("PaymentViewModel", "Exception during payment verification: ${e.message}", e)
                _paymentStatus.value = "Failed"
                _error.value = "Payment verification error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveTicketsToFirebase() {
        val details = bookingDetails
        val email = userEmail

        if (details == null || email == null) {
            Log.e(
                "PaymentViewModel",
                "Error: Booking details or user email missing. Details: $details, Email: $email"
            )
            _error.value = "Error: Booking details or user email missing"
            return
        }

        _isLoading.value = true

        viewModelScope.launch {
            try {
                val result = repository.saveTickets(details, email)
                if (result.isSuccess) {
                    val firstTicketHash = result.getOrNull()
                    _ticketHash.value = firstTicketHash
                } else {
                    val exception = result.exceptionOrNull()
                    Log.e(
                        "PaymentViewModel",
                        "Error saving tickets: ${exception?.message}",
                        exception
                    )
                    _error.value = "Error saving tickets: ${exception?.message ?: "Unknown error"}"
                }
            } catch (e: Exception) {
                Log.e("PaymentViewModel", "Exception during ticket saving: ${e.message}", e)
                _error.value = "Exception during ticket saving: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetState() {
        _isLoading.value = false
        _paymentStatus.value = null
        _ticketHash.value = null
        _error.value = null
    }
}

//class PaymentViewModel(private val repository: PaymentRepository) : ViewModel() {
//
//    private val _paymentStatus = MutableStateFlow<String?>(null)
//    val paymentStatus: StateFlow<String?> = _paymentStatus
//
//    private val _orderId = MutableStateFlow<String?>(null)
//    val orderId: StateFlow<String?> = _orderId
//
//    private val _paymentSessionId = MutableStateFlow<String?>(null)
//    val paymentSessionId: StateFlow<String?> = _paymentSessionId
//
//    private val _isLoading = MutableStateFlow(false)
//    val isLoading: StateFlow<Boolean> = _isLoading
//
//    private val _ticketHash = MutableStateFlow<String?>(null)
//    val ticketHash: StateFlow<String?> = _ticketHash
//
//    var bookingDetails: BookingDetails? = null
//    var userEmail: String? = null
//
//    fun setPaymentData(details: BookingDetails, email: String) {
//        bookingDetails = details
//        userEmail = email
//    }
//
//    fun setLoading(loading: Boolean) {
//        _isLoading.value = loading
//    }
//
//
//    fun startPaymentFlow(context: Context, amount: String, customerDetails: UserData) {
//        _isLoading.value = true
//        viewModelScope.launch {
//            val result = repository.createOrder(amount, customerDetails)
//            if (result.isSuccess) {
//                result.getOrNull()?.let { paymentModel ->
//                    _orderId.value = paymentModel.orderId
//                    _paymentSessionId.value = paymentModel.paymentSessionId
//
//                    val intent = Intent(context, PaymentVerificationActivity::class.java).apply {
//                        putExtra("ORDER_ID", paymentModel.orderId)
//                        putExtra("PAYMENT_SESSION_ID", paymentModel.paymentSessionId)
//                    }
//                    context.startActivity(intent)
//                } ?: run {
//                    _paymentStatus.value = "Failed"
//                }
//            } else {
//                _paymentStatus.value = "Failed"
//            }
//            _isLoading.value = false
//        }
//    }
//
//
//    fun verifyPayment(orderId: String) {
//        _isLoading.value = true
//        viewModelScope.launch {
//            val result = repository.verifyPayment(orderId)
//            if (result.isSuccess) {
//                val statusModel = result.getOrNull()
//                _paymentStatus.value = if (statusModel?.order_status == "PAID") "Paid" else "Failed"
//            } else {
//                _paymentStatus.value = "Failed"
//                Log.e("PaymentViewModel", "Verification error: ${result.exceptionOrNull()?.message}")
//            }
//            _isLoading.value = false
//        }
//    }
//
////    fun saveTicketsToFirebase() {
////        val details = bookingDetails
////        val email = userEmail
////        if (details == null || email == null) {
////            Log.e("PaymentViewModel", "Error: Booking details or user email missing.")
////            _paymentStatus.value = "Error: Booking details missing"
////            return
////        }
////
////        viewModelScope.launch {
////            val result = repository.saveTickets(details, email)
////            if (result.isSuccess) {
////                val firstTicketHash = result.getOrNull()
////                _ticketHash.value = firstTicketHash
////            } else {
////                Log.e("PaymentViewModel", "Error saving tickets: ${result.exceptionOrNull()?.message}")
////                _paymentStatus.value = "Error saving tickets"
////            }
////        }
////    }
//
//    fun saveTicketsToFirebase() {
//        val details = bookingDetails
//        val email = userEmail
//        if (details == null || email == null) {
//            Log.e("PaymentViewModel", "Error: Booking details or user email missing.")
//            _paymentStatus.value = "Error: Booking details missing"
//            return
//        }
//
//        viewModelScope.launch {
//            val result = repository.saveTickets(details, email)
//            if (result.isSuccess) {
//                val firstTicketHash = result.getOrNull()
//                _ticketHash.value = firstTicketHash
//                Log.d("PaymentViewModel", "Tickets saved successfully. Ticket hash: $firstTicketHash")
//                _paymentStatus.value = "Tickets Saved"
//            } else {
//                Log.e("PaymentViewModel", "Error saving tickets: ${result.exceptionOrNull()?.message}")
//                _paymentStatus.value = "Error saving tickets"
//            }
//        }
//    }
//
//    fun resetState() {
//        _isLoading.value = false
//        _paymentStatus.value = null
//        _ticketHash.value = null
//    }
//}


//class PaymentViewModel(
//    private val cashfreeApiService: CashfreeApiService
//) : ViewModel() {
//
//    internal val _paymentStatus = MutableLiveData<String?>()
//    val paymentStatus: LiveData<String?> = _paymentStatus
//
//    private var currentOrderId: String? = null
//    private var currentPaymentSessionId: String? = null
//
//    var bookingDetails: BookingDetails? = null
//    var userEmail: String? = null
//
//    val userId = FirebaseAuth.getInstance().currentUser?.uid
//
//    private val _ticketHash = MutableLiveData<String?>()
//    val ticketHash: LiveData<String?> = _ticketHash
//
//    private val _isLoading = MutableLiveData(false)
//    val isLoading: LiveData<Boolean> = _isLoading
//
//    // Set data before initiating the payment
//    fun setPaymentData(details: BookingDetails, email: String) {
//        bookingDetails = details
//        userEmail = email
//    }
//
//    fun setLoading(loading: Boolean) {
//        _isLoading.value = loading
//    }
//
//    private val _ticketDetails = MutableLiveData<Ticket?>()
//
//    private fun fetchTicketDetails(ticketHash: String, userId: String) {
//        val db = FirebaseFirestore.getInstance()
//        val ticketRef = db.collection("users").document(userId).collection("tickets").document(ticketHash)
//
//        ticketRef.get()
//            .addOnSuccessListener { document ->
//                if (document.exists()) {
//                    val ticket = document.toObject(Ticket::class.java)
//                    _ticketDetails.value = ticket
//                } else {
//                    Log.e("PaymentViewModel", "Ticket not found for hash: $ticketHash")
//                    _ticketDetails.value = null
//                }
//            }
//            .addOnFailureListener { e ->
//                Log.e("PaymentViewModel", "Error fetching ticket: ${e.message}")
//                _ticketDetails.value = null
//            }
//    }
//
//    fun setPaymentStatus(status: String) {
//        _paymentStatus.value = status
//        _isLoading.value = false
//    }
//
//    fun getCurrentOrderId() = currentOrderId
//    fun getCurrentPaymentSessionId() = currentPaymentSessionId
//
//
//    fun startPaymentFlow(context: Context, amount: String, customerDetails: UserData) {
//        _isLoading.value = true
//        cashfreeApiService.getOrderID(
//            Data(order_amount = amount.toDouble(), order_currency = "INR", customer_details = customerDetails)
//        ).enqueue(object : Callback<PaymentModel> {
//            override fun onResponse(call: Call<PaymentModel>, response: Response<PaymentModel>) {
//                response.body()?.let { paymentModel ->
//                    currentOrderId = paymentModel.orderId
//                    currentPaymentSessionId = paymentModel.paymentSessionId
//                    _isLoading.value = false
//
//                    // Automatically launch payment gateway
//                    val intent = Intent(context, PaymentVerificationActivity::class.java).apply {
//                        putExtra("ORDER_ID", currentOrderId)
//                        putExtra("PAYMENT_SESSION_ID", currentPaymentSessionId)
//                    }
//                    context.startActivity(intent)
//
//                } ?: run {
//                    _paymentStatus.value = "Failed"
//                    _isLoading.value = false
//                }
//            }
//
//            override fun onFailure(call: Call<PaymentModel>, t: Throwable) {
//                _paymentStatus.value = "Failed"
//                _isLoading.value = false
//            }
//        })
//    }
//
//
//    fun verifyPayment(orderId: String) {
//        _isLoading.value = true
//        cashfreeApiService.create(orderId).enqueue(object : Callback<PaymentStatusModel> {
//            override fun onResponse(call: Call<PaymentStatusModel>, response: Response<PaymentStatusModel>) {
//                if (response.isSuccessful) {
//                    val statusModel = response.body()
//                    if (statusModel?.order_status == "PAID") {
//                        _paymentStatus.value = "Paid"
//                    } else {
//                        _paymentStatus.value = "Failed"
//                        Log.e("PaymentViewModel", "Payment verification failed: ${statusModel?.order_status}")
//                    }
//                } else {
//                    _paymentStatus.value = "Failed"
//                    Log.e("PaymentViewModel", "Verification error: ${response.errorBody()?.string()}")
//                }
//                _isLoading.value = false
//            }
//
//            override fun onFailure(call: Call<PaymentStatusModel>, t: Throwable) {
//                _paymentStatus.value = "Verification failed: ${t.message}"
//                Log.e("PaymentViewModel", "Payment verification error: ${t.message}", t)
//                _isLoading.value = false
//            }
//        })
//    }
//
//    fun saveTicketsToFirebase() {
//        val details = bookingDetails
//        val email = userEmail
//        val userId = userId // Replace with logic to fetch logged-in user ID
//
//        if (details == null || email == null || userId == null) {
//            Log.e("PaymentViewModel", "Error: Booking details or user email missing.")
//            _paymentStatus.value = "Error: Booking details missing"
//            return
//        }
//
//        val db = FirebaseFirestore.getInstance()
//        val userTicketsRef = db.collection("users").document(userId).collection("tickets")
//
//        // Map each seat to a corresponding ticket
//        val tickets = details.seats.map { seat ->
//            val ticketHash = generateTicketHash("${details.eventId}|$seat|$email")
//            Ticket(
//                eventId = details.eventId,
//                seatNumber = seat,
//                ownerEmail = email,
//                ticketHash = ticketHash,
//                eventName = details.title,
//                language = details.language,
//                purchaseTimestamp = System.currentTimeMillis(),
//                isForSale = false,
//                resalePrice = null
//            )
//        }
//
//        val batch = db.batch()
//        val tasks = mutableListOf<Task<Void>>() // To hold all tasks for proper sequencing
//
//        tickets.forEach { ticket ->
//            val docRef = userTicketsRef.document(ticket.ticketHash)
//
//            // Fetch existing ticket and update seat info if it already exists
//            val task = docRef.get().continueWithTask { task ->
//                val document = task.result
//                if (document.exists()) {
//                    // Update the existing document
//                    batch.update(docRef, "seatNumber", ticket.seatNumber, "purchaseTimestamp", ticket.purchaseTimestamp)
//                } else {
//                    // Create a new document
//                    batch.set(docRef, ticket)
//                }
//                // Return a Task<Void> to satisfy the return type of continueWithTask
//                Tasks.forResult<Void>(null)
//            }
//
//            tasks.add(task) // Add the task to the list
//        }
//
//        // Commit the batch after all operations have been added and tasks are complete
//        Tasks.whenAllSuccess<Void>(tasks).addOnCompleteListener {
//            batch.commit().addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    _paymentStatus.value = "Tickets saved successfully!"
//                    val firstTicketHash = tickets.firstOrNull()?.ticketHash
//                    _ticketHash.value = firstTicketHash
//                    firstTicketHash?.let { fetchTicketDetails(it, userId) }
//                } else {
//                    Log.e("PaymentViewModel", "Error saving tickets: ${task.exception?.message}")
//                    _paymentStatus.value = "Error saving tickets: ${task.exception?.message}"
//                }
//            }
//        }
//    }
//
//    private fun generateTicketHash(data: String): String {
//        val digest = MessageDigest.getInstance("SHA-256")
//        return digest.digest(data.toByteArray()).joinToString("") { "%02x".format(it) }
//    }
//
//    fun resetState() {
//        _isLoading.value = false
//        _paymentStatus.value = null
//        _ticketHash.value = null
//        currentOrderId = null
//        currentPaymentSessionId = null
//    }
//
//}
