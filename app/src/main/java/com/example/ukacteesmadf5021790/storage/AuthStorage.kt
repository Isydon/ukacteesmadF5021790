package com.example.ukacteesmadf5021790.storage

import com.google.firebase.auth.FirebaseAuth

// Handles Firebase Authentication actions such as login, sign up and logout.
object AuthStorage {

    private val auth = FirebaseAuth.getInstance()

    // Returns the current user's unique Firebase ID.
    fun currentUserId(): String? {
        return auth.currentUser?.uid
    }

    // Checks if a user is already logged in.
    fun isLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    // Creates a new user account.
    fun signUp(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { error ->
                onFailure(error.message ?: "Sign up failed")
            }
    }

    // Logs in an existing user.
    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { error ->
                onFailure(error.message ?: "Login failed")
            }
    }

    // Logs out the current user.
    fun logout() {
        auth.signOut()
    }
}