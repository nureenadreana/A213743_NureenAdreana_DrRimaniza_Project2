package com.example.a213743_nureenadreana_drrimaniza_lab1.repository

import com.example.a213743_nureenadreana_drrimaniza_lab1.data.FoodDao
import com.example.a213743_nureenadreana_drrimaniza_lab1.data.FoodEntity
import kotlinx.coroutines.flow.Flow

class FoodRepository(
    private val foodDao: FoodDao
) {
    val allItems: Flow<List<FoodEntity>> = foodDao.getAllItems()

    fun getItemsByUser(userName: String): Flow<List<FoodEntity>> =
        foodDao.getItemsByUser(userName)

    suspend fun insert(item: FoodEntity) {
        foodDao.insertItem(item)
    }

    suspend fun delete(item: FoodEntity) {
        foodDao.deleteItem(item)
    }

    suspend fun update(item: FoodEntity) {
        foodDao.updateItem(item)
    }
}
