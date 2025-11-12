package com.example.fitnessway.data.network.food

import com.example.fitnessway.data.model.food.FoodLogAddPostResponse
import com.example.fitnessway.data.model.food.FoodLogAddRequest
import com.example.fitnessway.data.model.food.FoodLogDeleteResponse
import com.example.fitnessway.data.model.food.FoodLogsApiFetchResponse
import com.example.fitnessway.data.model.food.FoodsApiFetchResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface IFoodApiService {
    @GET("food/get-foods")
    suspend fun getFoods(): Response<FoodsApiFetchResponse>

    @GET("food/log/get-logs")
    suspend fun getFoodLogs(
        @Query("date") date: String
    ): Response<FoodLogsApiFetchResponse>

    @POST("food/log/add")
    suspend fun addFoodLog(
        @Body request: FoodLogAddRequest
    ): Response<FoodLogAddPostResponse>

    @DELETE("food/log/delete")
    suspend fun deleteFoodLog(
        @Query("food-log-id") foodLogId: Int
    ): Response<FoodLogDeleteResponse>
}
