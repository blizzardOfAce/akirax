package com.example.akirax.domain.usecase

import com.example.akirax.domain.model.User
import com.example.akirax.domain.repository.AuthRepository

data class AuthUseCases(
    val registerUser: RegisterUser,
    val loginUser: LoginUser,
    val checkUserLoggedIn: CheckUserLoggedIn,
    val logout: Logout
)

class RegisterUser(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Result<User> =
        repository.registerUser(email, password)
}

class LoginUser(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Result<User> =
        repository.loginUser(email, password)
}

class CheckUserLoggedIn(private val repository: AuthRepository) {
    suspend operator fun invoke(): Boolean = repository.isUserLoggedIn()
}

class Logout(private val repository: AuthRepository) {
    suspend operator fun invoke() = repository.logout()
}

