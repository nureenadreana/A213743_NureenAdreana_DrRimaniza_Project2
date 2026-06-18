package com.example.a213743_nureenadreana_drrimaniza_lab1.data

data class UserProfile(
    val name: String = "",
    val username: String = "",
    val profileImageUri: String? = null,
    val listedCount: Int = 0,
    val claimedCount: Int = 0,
    val rating: Double = 0.0
)
