package com.example.akirax.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.akirax.domain.model.Resource
import com.example.akirax.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class ProfileViewModel(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _userState = MutableStateFlow<User?>(null)
    val userState: StateFlow<User?> get() = _userState

    private val _updateState = MutableStateFlow<Resource<String>>(Resource.Idle)
    val updateState: StateFlow<Resource<String>> get() = _updateState

    init {
        fetchUserData()
    }

    private fun fetchUserData() {
        val currentUser: FirebaseUser? = firebaseAuth.currentUser
        if (currentUser != null) {
            firestore.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val user = User(
                            uid = currentUser.uid,
                            name = document.getString("name"),
                            email = currentUser.email,
                            photoUrl = document.getString("photoUrl")
                        )
                        _userState.update { user }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("ProfileViewModel", "Error fetching user data: ${e.message}")
                }
        }
    }

    fun updateUserProfile(name: String, photoUrl: String?) {
        val currentUser = firebaseAuth.currentUser ?: return
        val updates = mutableMapOf<String, Any>("name" to name)
        photoUrl?.let { updates["photoUrl"] = it }

        _updateState.update { Resource.Loading }
        firestore.collection("users").document(currentUser.uid).update(updates)
            .addOnSuccessListener {
                _userState.update {
                    it?.copy(name = name, photoUrl = photoUrl)
                }
                _updateState.update { Resource.Success("Profile updated successfully.") }
            }
            .addOnFailureListener { e ->
                _updateState.update { Resource.Error("Failed to update profile: ${e.message}") }
            }
    }

    fun clearData() {
        _userState.value = null
    }
}
