package com.example.a213743_nureenadreana_drrimaniza_lab1.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val itemName: String,
    val userName: String,
    val location: String,
    val price: String,
    val isFree: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
