package com.example.a213743_nureenadreana_drrimaniza_lab1.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_items") //nama table
data class FoodEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, //auto generate id

    val title: String,
    val distance: String,
    val imageUri: String?, // Store URI as string
    val userName: String,
    val isFree: Boolean,
    val description: String,
    val price: String = ""
)