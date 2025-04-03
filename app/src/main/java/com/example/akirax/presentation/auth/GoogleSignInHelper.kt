package com.example.akirax.presentation.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.example.akirax.data.ApiKeys
import com.example.akirax.domain.model.User
import com.example.akirax.presentation.viewmodel.AuthState
import com.example.akirax.presentation.viewmodel.AuthUiState
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val TAG = "GoogleSignIn"
class GoogleSignInHelper(
    private val context: Context,
    private val updateUiState: (AuthUiState) -> Unit,
    private val startAddAccountIntentLauncher: ActivityResultLauncher<Intent>?
) {
    private val credentialManager = CredentialManager.create(context)
    private val auth = FirebaseAuth.getInstance()
    private val firebaseWebClientId = ApiKeys.firebaseWebClientId
    fun signInWithGoogle(isForLogin: Boolean) {
        val activity = context as? Activity
        if (activity == null) {
            Log.e(TAG, "Context is not an Activity")
            updateUiState(AuthUiState(authState = AuthState.Error("Invalid context")))
            return
        }

        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(firebaseWebClientId)
            .setAutoSelectEnabled(isForLogin)  // Auto-select enabled only for login
            .setFilterByAuthorizedAccounts(isForLogin)  // Filter only authorized accounts for login
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = credentialManager.getCredential(activity, request)
                handleSignIn(result)
            } catch (e: Exception) {
                handleSignInError(e)
            }
        }
    }

    // Handle successful credential retrieval
    private fun handleSignIn(result: GetCredentialResponse) {
        val credential = result.credential
        when (credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)
                        val idToken = googleIdTokenCredential.idToken
                        CoroutineScope(Dispatchers.IO).launch {
                            authenticateWithFirebase(idToken)
                        }
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e(TAG, "Invalid Google ID token response", e)
                        updateUiState(AuthUiState(authState = AuthState.Error("Invalid token response")))
                    }
                } else {
                    Log.e(TAG, "Unexpected credential type")
                }
            }

            else -> Log.e(TAG, "Unrecognized credential type")
        }
    }

    //Authenticate user and save user data to firestore
    private suspend fun authenticateWithFirebase(idToken: String) {
        updateUiState(AuthUiState(isLoading = true))
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        try {
            val result = auth.signInWithCredential(credential).await()
            val firebaseUser = result.user ?: throw Exception("User object is null after sign-in")

            // Create a User object to save to Firestore
            val user = User(
                uid = firebaseUser.uid,
                email = firebaseUser.email ?: "",
                name = firebaseUser.displayName ?: "Anonymous",
                photoUrl = firebaseUser.photoUrl?.toString()
            )

            // Save the user to Firestore
            saveUserToFirestore(user)

            // Update the UI state to indicate success
            updateUiState(
                AuthUiState(
                    isAuthenticated = true,
                    user = user,
                    authState = AuthState.Success(user)
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Authentication failed", e)
            updateUiState(
                AuthUiState(authState = AuthState.Error("Authentication failed: ${e.message}"))
            )
        }
    }

    private suspend fun saveUserToFirestore(user: User) {
        try {
            val firestore = FirebaseFirestore.getInstance()
            firestore.collection("users")
                .document(user.uid)  // Use UID as the document ID
                .set(user, SetOptions.merge())  // Prevent overwriting existing data
                .await()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save user to Firestore: ${e.message}", e)
            throw e  // Re-throw to handle in the calling function
        }
    }

    private fun handleSignInError(e: Exception) {
        val message = when (e) {
            is NoCredentialException -> "No credentials found."
            is GetCredentialCancellationException -> {
                Log.e(TAG, "User cancelled the sign-in.")
                "Sign-in cancelled. Please try again."
            }

            is GetCredentialException -> "Failed to retrieve credentials."
            else -> "Unexpected error: ${e.message}"
        }
        Log.e(TAG, message, e)
        updateUiState(AuthUiState(authState = AuthState.Error(message)))
    }

    // Helper function to generate a random nonce (optional for security)
    private fun generateNonce(): String {
        val charset = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
        return (1..32).map { charset.random() }.joinToString("")
    }
}
