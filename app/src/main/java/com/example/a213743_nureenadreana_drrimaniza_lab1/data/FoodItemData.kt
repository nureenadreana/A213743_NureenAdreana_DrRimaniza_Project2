package com.example.a213743_nureenadreana_drrimaniza_lab1.data

data class FoodItemData(
    val id: String = "",
    val title: String,
    val distance: String,
    val imageUri: String?,
    val userName: String,
    val isFree: Boolean = true,
    val description: String = "",
    val price: String = ""
)
