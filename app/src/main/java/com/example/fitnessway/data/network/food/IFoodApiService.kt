package com.example.fitnessway.data.network.food

import com.example.fitnessway.data.model.MFood.Api.Req.FoodAddRequest
import com.example.fitnessway.data.model.MFood.Api.Req.FoodLogAddRequest
import com.example.fitnessway.data.model.MFood.Api.Req.FoodLogUpdateRequest
import com.example.fitnessway.data.model.MFood.Api.Req.FoodUpdateRequest
import com.example.fitnessway.data.model.MFood.Api.Res.FoodAddApiResponse
import com.example.fitnessway.data.model.MFood.Api.Res.FoodDeleteApiResponse
import com.example.fitnessway.data.model.MFood.Api.Res.FoodLogAddApiResponse
import com.example.fitnessway.data.model.MFood.Api.Res.FoodLogDeleteApiResponse
import com.example.fitnessway.data.model.MFood.Api.Res.FoodLogUpdateApiResponse
import com.example.fitnessway.data.model.MFood.Api.Res.FoodLogsGetApiResponse
import com.example.fitnessway.data.model.MFood.Api.Res.FoodUpdateApiResponse
import com.example.fitnessway.data.model.MFood.Api.Res.FoodsGetApiResponse
import com.example.fitnessway.data.model.api.ApiResponseWithContent
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface IFoodApiService {
    @GET("food/get-foods")
    suspend fun getFoods(): Response<ApiResponseWithContent<FoodsGetApiResponse>>

    @POST("food/add")
    suspend fun addFood(
        @Body request: FoodAddRequest
    ): Response<ApiResponseWithContent<FoodAddApiResponse>>

    @PUT("food/update")
    suspend fun updateFood(
        @Body request: FoodUpdateRequest
    ): Response<ApiResponseWithContent<FoodUpdateApiResponse>>

    @DELETE("food/delete")
    suspend fun deleteFood(
        @Query("food-id") foodId: Int
    ): Response<ApiResponseWithContent<FoodDeleteApiResponse>>

    @GET("food/log/get-logs")
    suspend fun getFoodLogs(
        @Query("date") date: String
    ): Response<ApiResponseWithContent<FoodLogsGetApiResponse>>

    @POST("food/log/add")
    suspend fun addFoodLog(
        @Body request: FoodLogAddRequest
    ): Response<ApiResponseWithContent<FoodLogAddApiResponse>>

    @PUT("food/log/update")
    suspend fun updateFoodLog(
        @Body request: FoodLogUpdateRequest
    ): Response<ApiResponseWithContent<FoodLogUpdateApiResponse>>

    @DELETE("food/log/delete")
    suspend fun deleteFoodLog(
        @Query("food-log-id") foodLogId: Int
    ): Response<ApiResponseWithContent<FoodLogDeleteApiResponse>>
}
