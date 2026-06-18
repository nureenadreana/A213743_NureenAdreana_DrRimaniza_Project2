package com.example.a213743_nureenadreana_drrimaniza_lab1.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: FoodEntity)

    @Query("SELECT * FROM food_items ORDER BY id DESC")
    fun getAllItems(): Flow<List<FoodEntity>>

    @Query("SELECT * FROM food_items WHERE userName = :userName ORDER BY id DESC")
    fun getItemsByUser(userName: String): Flow<List<FoodEntity>>

    @Delete
    suspend fun deleteItem(item: FoodEntity)

    @Update
    suspend fun updateItem(item: FoodEntity)
}
