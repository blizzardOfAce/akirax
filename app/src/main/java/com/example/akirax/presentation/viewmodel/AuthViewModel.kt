package com.example.akirax.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.akirax.domain.model.User
import com.example.akirax.domain.usecase.AuthUseCases
import com.example.akirax.utils.toUser
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class AuthViewModel(private val useCases: AuthUseCases) : ViewModel() {

    private val _authState = MutableStateFlow(AuthUiState())
    val authState: StateFlow<AuthUiState> = _authState.asStateFlow()

    init {
        checkUserLoginState()
    }

    private fun checkUserLoginState() {
        viewModelScope.launch {
            val isLoggedIn = useCases.checkUserLoggedIn()
            val user = if (isLoggedIn) FirebaseAuth.getInstance().currentUser?.toUser() else null

            _authState.update {
                it.copy(
                    isAuthenticated = isLoggedIn,
                    user = user,
                    authState = if (isLoggedIn) AuthState.Success(user!!) else AuthState.Idle
                )
            }
            if (isLoggedIn && user != null) {
                fetchUserData(user) // Fetch user data after checking login state
            }
        }
    }

    // Fetch user data after login
    private fun fetchUserData(user: User) {
        _authState.update {
            it.copy(
                user = user,
                isAuthenticated = true,
                authState = AuthState.Success(user)
            )
        }
    }

    fun loginUser(email: String, password: String) {
        performAuthOperation { useCases.loginUser(email, password) }
    }

    fun logout() {
        viewModelScope.launch {
            useCases.logout()
            _authState.value = AuthUiState() // Reset the state
        }
    }

    fun registerUser(email: String, password: String) {
        performAuthOperation { useCases.registerUser(email, password) }
    }

    private fun performAuthOperation(operation: suspend () -> Result<User>) {
        viewModelScope.launch {
            _authState.update { it.copy(isLoading = true) }
            val result = operation()
            Log.e("AuthViewModel", "Auth operation result: ${result.isSuccess}")
            _authState.update {
                if (result.isSuccess) {
                    val user = result.getOrNull()
                    it.copy(
                        user = user,
                        isAuthenticated = user != null,
                        isLoading = false,
                        authState = AuthState.Success(user ?: User("empty", "No user", null, null))
                    )
                } else {
                    it.copy(
                        isLoading = false,
                        authState = AuthState.Error(result.exceptionOrNull()?.message ?: "Operation failed.")
                    )
                }
            }
        }
    }

    fun updateAuthState(uiState: AuthUiState) {
        _authState.value = uiState
    }
}

data class AuthUiState(
    val alreadySignUp: Boolean = false,
    val isLoading: Boolean = false,
    val user: User? = null,
    val isAuthenticated: Boolean = false,
    val authState: AuthState = AuthState.Idle
)

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}




