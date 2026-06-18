package com.example.a213743_nureenadreana_drrimaniza_lab1.repository

import android.util.Log
import com.example.a213743_nureenadreana_drrimaniza_lab1.data.FoodItemData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

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
        onComplete: (Boolean) -> Unit
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
                onComplete(true)
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseRepository", "Error saving listing", e)
                onComplete(false)
            }
    }

    // Update existing food listing
    fun updateFoodListing(
        id: String,
        title: String,
        location: String,
        isFree: Boolean,
        description: String,
        price: String,
        imageUri: String?,
        onComplete: (Boolean) -> Unit
    ) {
        val updates = hashMapOf<String, Any?>(
            "title" to title,
            "location" to location,
            "isFree" to isFree,
            "description" to description,
            "price" to price,
            "imageUri" to imageUri
        )
        db.collection("food_listings")
            .document(id)
            .update(updates)
            .addOnSuccessListener {
                Log.d("FirebaseRepository", "Listing updated successfully")
                onComplete(true)
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseRepository", "Error updating listing", e)
                onComplete(false)
            }
    }

    // Delete food listing
    fun deleteListing(id: String, onComplete: (Boolean) -> Unit) {
        db.collection("food_listings")
            .document(id)
            .delete()
            .addOnSuccessListener {
                Log.d("FirebaseRepository", "Listing deleted successfully")
                onComplete(true)
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseRepository", "Error deleting listing", e)
                onComplete(false)
            }
    }

    // Fetch all listings as a Flow
    fun getListingsFlow(): Flow<List<FoodItemData>> = callbackFlow {
        val subscription = db.collection("food_listings")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val items = snapshot.documents.mapNotNull { doc ->
                        try {
                            FoodItemData(
                                id = doc.id,
                                title = doc.getString("title") ?: "",
                                distance = doc.getString("location") ?: "",
                                imageUri = doc.getString("imageUri"),
                                userName = doc.getString("userName") ?: "",
                                isFree = doc.getBoolean("isFree") ?: true,
                                description = doc.getString("description") ?: "",
                                price = doc.getString("price") ?: ""
                            )
                        } catch (ex: Exception) {
                            null
                        }
                    }
                    trySend(items)
                }
            }
        awaitClose { subscription.remove() }
    }

    // Fetch all community listings from Firebase (Legacy callback version)
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
                        data?.put("id", doc.id)
                        data
                    }
                    onResult(list)
                }
            }
    }
}
