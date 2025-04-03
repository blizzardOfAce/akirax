package com.example.akirax.data.repository

import com.example.akirax.domain.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreRepository(private val firestore: FirebaseFirestore) {

    suspend fun saveUser(user: User) {
        try {
            firestore.collection("users").document(user.uid).set(user).await()
        } catch (e: Exception) {
            throw Exception("Failed to save user: ${e.message}")
        }
    }

}
