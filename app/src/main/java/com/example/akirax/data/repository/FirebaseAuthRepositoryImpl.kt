package com.example.akirax.data.repository

import com.example.akirax.domain.model.User
import com.example.akirax.domain.repository.AuthRepository
import com.example.akirax.utils.toUser
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestoreRepository: FirestoreRepository
) : AuthRepository {

    override suspend fun registerUser(email: String, password: String): Result<User> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw Exception("User creation failed")

            val user = User(
                uid = firebaseUser.uid,
                name = firebaseUser.displayName ?: "Anonymous",
                email = firebaseUser.email ?: "",
                photoUrl = firebaseUser.photoUrl?.toString()
            )

            saveUser(user)  // Save user to Firestore

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveUser(user: User) {
        firestoreRepository.saveUser(user)
    }

    override suspend fun loginUser(email: String, password: String): Result<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user
            firebaseUser?.toUser()?.let { Result.success(it) }
                ?: Result.failure(Exception("Login failed: User not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    override suspend fun logout() {
        auth.signOut()
    }
}



