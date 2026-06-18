package com.example.a213743_nureenadreana_drrimaniza_lab1.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// Data classes
data class WgerResponse(
    val results: List<WgerItem>
)

data class WgerItem(
    val name: String,
    val energy: Double?,
    val protein: String?,
    val carbohydrates: String?,
    val fat: String?,
    val fiber: String?,
    val sugar: String?
)

// Retrofit interface
interface WgerApi {
    @GET("api/v2/ingredient/")
    suspend fun searchFood(
        @Query("format") format: String = "json",
        @Query("language") language: Int = 2,
        @Query("name") name: String
    ): WgerResponse
}

// Singleton
object WgerInstance {
    val api: WgerApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://wger.de/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WgerApi::class.java)
    }
}
