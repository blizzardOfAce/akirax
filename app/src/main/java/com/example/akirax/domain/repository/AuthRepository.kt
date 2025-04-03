package com.example.akirax.domain.repository

import com.example.akirax.domain.model.User

interface AuthRepository {
    suspend fun registerUser(email: String, password: String): Result<User>
    suspend fun loginUser(email: String, password: String): Result<User>
    suspend fun isUserLoggedIn(): Boolean
    suspend fun logout()
    suspend fun saveUser(user: User)
}

