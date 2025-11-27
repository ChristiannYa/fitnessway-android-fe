package com.example.fitnessway.data.network.food

import com.example.fitnessway.data.model.food.FoodAddApiPostResponse
import com.example.fitnessway.data.model.food.FoodAddRequest
import com.example.fitnessway.data.model.food.FoodLogAddPostResponse
import com.example.fitnessway.data.model.food.FoodLogAddRequest
import com.example.fitnessway.data.model.food.FoodLogDeleteResponse
import com.example.fitnessway.data.model.food.FoodLogUpdatePutResponse
import com.example.fitnessway.data.model.food.FoodLogUpdateRequest
import com.example.fitnessway.data.model.food.FoodLogsApiFetchResponse
import com.example.fitnessway.data.model.food.FoodUpdateApiPutResponse
import com.example.fitnessway.data.model.food.FoodUpdateRequest
import com.example.fitnessway.data.model.food.FoodsApiFetchResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface IFoodApiService {
    @GET("food/get-foods")
    suspend fun getFoods(): Response<FoodsApiFetchResponse>

    @POST("food/create")
    suspend fun addFood(
        @Body request: FoodAddRequest
    ): Response<FoodAddApiPostResponse>

    @PUT("food/update")
    suspend fun updateFood(
        @Body request: FoodUpdateRequest
    ): Response<FoodUpdateApiPutResponse>

    @GET("food/log/get-logs")
    suspend fun getFoodLogs(
        @Query("date") date: String
    ): Response<FoodLogsApiFetchResponse>

    @POST("food/log/add")
    suspend fun addFoodLog(
        @Body request: FoodLogAddRequest
    ): Response<FoodLogAddPostResponse>

    @PUT("food/log/update")
    suspend fun updateFoodLog(
        @Body request: FoodLogUpdateRequest
    ): Response<FoodLogUpdatePutResponse>

    @DELETE("food/log/delete")
    suspend fun deleteFoodLog(
        @Query("food-log-id") foodLogId: Int
    ): Response<FoodLogDeleteResponse>
}
