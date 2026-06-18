package com.example.a213743_nureenadreana_drrimaniza_lab1.data

import androidx.room.Entity

@Entity(tableName = "bookmarks", primaryKeys = ["foodId", "userName"])
data class BookmarkEntity(
    val foodId: Int,
    val userName: String
)
