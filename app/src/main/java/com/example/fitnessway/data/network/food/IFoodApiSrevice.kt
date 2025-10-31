package com.example.fitnessway.data.network.food

import com.example.fitnessway.data.model.food.FoodLogsApiFetchResponse
import com.example.fitnessway.data.model.food.FoodsApiFetchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface IFoodApiService {
    @GET("food/get-foods")
    suspend fun getFoods(): Response<FoodsApiFetchResponse>

    @GET("food/log/get-logs")
    suspend fun getFoodLogs(
        @Query("date") date: String
    ): Response<FoodLogsApiFetchResponse>
}
