package com.example.akirax.presentation.viewmodel

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.akirax.data.repository.FirebaseTicketsRepository
import com.example.akirax.data.repository.TicketsRepository
import com.example.akirax.domain.model.Ticket
import com.google.firebase.auth.FirebaseAuth
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TicketsViewModel(
    private val ticketsRepository: TicketsRepository = FirebaseTicketsRepository()
) : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    private val _ticket = MutableStateFlow<Ticket?>(null)
    val ticket: StateFlow<Ticket?> = _ticket

    private val _tickets = MutableStateFlow<List<Ticket>>(emptyList())
    val tickets: StateFlow<List<Ticket>> = _tickets

    private val _resaleTickets = MutableStateFlow<List<Ticket>>(emptyList())
    val resaleTickets: StateFlow<List<Ticket>> = _resaleTickets

    private val _qrCode = MutableStateFlow<Bitmap?>(null)
    val qrCode: StateFlow<Bitmap?> = _qrCode

    private val _ticketHash = MutableStateFlow<String?>(null)
    val ticketHash: StateFlow<String?> = _ticketHash

    init{
        viewModelScope.launch{ fetchAllTickets() }
    }

    suspend fun fetchTicketHashByEventId(eventId: String) {
        val userId = auth.currentUser?.uid ?: return
        _ticketHash.value = ticketsRepository.fetchTicketHashByEventId(userId, eventId)
    }

    val boughtTickets = tickets.map { it.map { ticket -> ticket.eventId }.toSet() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptySet())

    suspend fun fetchTicket(ticketHash: String) {
        val userId = auth.currentUser?.uid ?: return
        val fetchedTicket = ticketsRepository.fetchTicket(userId, ticketHash)
        _ticket.value = fetchedTicket
        if (fetchedTicket != null && _qrCode.value == null) {
            _qrCode.value = generateQRCode(fetchedTicket.ticketHash)
        }
    }

    suspend fun fetchAllTickets() {
        val userId = auth.currentUser?.uid ?: return
        _tickets.value = ticketsRepository.fetchAllTickets(userId)
    }


    suspend fun fetchResaleTickets() {
        _resaleTickets.value = ticketsRepository.fetchResaleTickets()
    }

    suspend fun markTicketForResale(ticketHash: String, resalePrice: Double) {
        val userId = auth.currentUser?.uid ?: return
        val ticket = _tickets.value.firstOrNull { it.ticketHash == ticketHash } ?: return
        Log.d("TicketsViewModel", "Ticket: $ticket")
        ticketsRepository.markTicketForResale(userId, ticket, resalePrice)
        fetchResaleTickets()
    }

    suspend fun cancelBooking(ticket: Ticket) {
        val userId = auth.currentUser?.uid ?: return
        ticketsRepository.cancelBooking(userId, ticket)
        fetchAllTickets()
    }

    //Later
    suspend fun cancelResale(ticketHash: String) {
        val userId = auth.currentUser?.uid ?: return
        ticketsRepository.cancelResale(userId, ticketHash)
        fetchResaleTickets()
    }

    private fun generateQRCode(text: String, size: Int = 512): Bitmap {
        if (text.isBlank()) return createBitmap(size, size, Bitmap.Config.RGB_565)
        val writer = QRCodeWriter()
        val hints = mapOf(EncodeHintType.MARGIN to 1)
        val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, size, size, hints)
        val bitmap = createBitmap(size, size, Bitmap.Config.RGB_565)
        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap[x, y] =
                    if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE
            }
        }
        return bitmap
    }
}