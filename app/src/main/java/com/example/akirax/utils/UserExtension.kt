package com.example.akirax.utils

import com.example.akirax.domain.model.User
import com.google.firebase.auth.FirebaseUser

fun FirebaseUser.toUser(): User {
    return User(
        uid = uid,
        email = email,
        name = displayName,
        photoUrl = photoUrl?.toString()
    )
}
