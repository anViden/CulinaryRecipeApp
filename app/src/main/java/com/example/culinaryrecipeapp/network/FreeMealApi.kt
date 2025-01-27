package com.example.culinaryrecipeapp.network

import com.example.culinaryrecipeapp.network.models.MealResponse
import retrofit2.http.GET

interface FreeMealApi {
    @GET("random.php")
    suspend fun getRandomMeal(): MealResponse
}
