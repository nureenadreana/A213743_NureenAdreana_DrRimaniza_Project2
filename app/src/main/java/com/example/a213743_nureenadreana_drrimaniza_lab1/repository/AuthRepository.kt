package com.example.a213743_nureenadreana_drrimaniza_lab1.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun register(
        email: String,
        password: String,
        name: String,
        username: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid
                if (uid != null) {
                    // Simpan profil ke Firestore selepas register berjaya
                    saveUserProfile(uid, name, username, null)
                }
                onSuccess()
            }
            .addOnFailureListener {
                onFailure(it.message ?: "Unknown error")
            }
    }

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure(it.message ?: "Unknown error")
            }
    }

    fun logout() {
        auth.signOut()
    }

    fun currentUser() = auth.currentUser

    // --- Profile Logic ---

    fun saveUserProfile(uid: String, name: String, username: String, profileImageUri: String?) {
        val userProfile = hashMapOf(
            "name" to name,
            "username" to username,
            "profileImageUri" to profileImageUri
        )
        db.collection("users").document(uid).set(userProfile)
    }

    fun getUserProfile(uid: String, onResult: (Map<String, Any>?) -> Unit) {
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                onResult(document.data)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }
}
