package com.example.a213743_nureenadreana_drrimaniza_lab1.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

object FirebaseRepository {
    private val db = FirebaseFirestore.getInstance()

    // Save food listing to Firebase when user posts
    fun saveFoodListing(
        title: String,
        location: String,
        userName: String,
        isFree: Boolean,
        description: String,
        price: String,
        imageUri: String?,
        onComplete: () -> Unit
    ) {
        val listing = hashMapOf(
            "title" to title,
            "location" to location,
            "userName" to userName,
            "isFree" to isFree,
            "description" to description,
            "price" to price,
            "imageUri" to imageUri,
            "timestamp" to System.currentTimeMillis()
        )
        db.collection("food_listings")
            .add(listing)
            .addOnSuccessListener {
                Log.d("FirebaseRepository", "Listing saved successfully")
                onComplete()
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseRepository", "Error saving listing", e)
            }
    }

    // Fetch all community listings from Firebase
    fun getAllListings(onResult: (List<Map<String, Any>>) -> Unit) {
        db.collection("food_listings")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("FirebaseRepository", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        val data = doc.data?.toMutableMap()
                        data?.put("id", doc.id) // Simpan ID dokumen jika perlu
                        data
                    }
                    onResult(list)
                }
            }
    }
}
