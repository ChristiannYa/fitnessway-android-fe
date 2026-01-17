package com.example.fitnessway.data.network

import com.example.fitnessway.data.model.MApi.Model.ApiResponse
import com.example.fitnessway.data.model.MApi.Model.ApiResponseWithContent
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query
import com.example.fitnessway.data.model.MAuth.Api.Req as AReq
import com.example.fitnessway.data.model.MAuth.Api.Res as ARes
import com.example.fitnessway.data.model.MFood.Api.Req as FReq
import com.example.fitnessway.data.model.MFood.Api.Res as FRes
import com.example.fitnessway.data.model.MNutrient.Api.Req as NReq
import com.example.fitnessway.data.model.MNutrient.Api.Res as NRes
import com.example.fitnessway.data.model.MUser.Api.Res as URes

object RetrofitService {
    interface IAuthorized {
        @POST("auth/logout")
        suspend fun logout(
            @Body request: AReq.LogoutRequest
        ): Response<ApiResponse>
    }

    interface IUnauthorized {
        @POST("auth/login")
        suspend fun login(
            @Body request: AReq.LoginRequest
        ): Response<ApiResponseWithContent<ARes.LoginApiResponse>>

        @POST("auth/register")
        suspend fun register(
            @Body request: AReq.RegisterRequest
        ): Response<ApiResponseWithContent<ARes.RegisterApiResponse>>
    }

    interface IUser {
        @GET("user/me")
        suspend fun getUser(): Response<ApiResponseWithContent<URes.UserApiGetResponse>>
    }

    interface IFoodApiService {
        @GET("food/get-foods")
        suspend fun getFoods(): Response<ApiResponseWithContent<FRes.FoodsGetApiResponse>>

        @POST("food/add")
        suspend fun addFood(
            @Body request: FReq.FoodAddRequest
        ): Response<ApiResponseWithContent<FRes.FoodAddApiResponse>>

        @PUT("food/update")
        suspend fun updateFood(
            @Body request: FReq.FoodUpdateRequest
        ): Response<ApiResponseWithContent<FRes.FoodUpdateApiResponse>>

        @DELETE("food/delete")
        suspend fun deleteFood(
            @Query("food-id") foodId: Int
        ): Response<ApiResponseWithContent<FRes.FoodDeleteApiResponse>>

        @PUT("food/update-favorite-status")
        suspend fun updateFoodFavoriteStatus(
            @Body request: FReq.FoodFavoriteStatusUpdateRequest
        ): Response<ApiResponseWithContent<FRes.FoodFavoriteStatusUpdateApiResponse>>

        @GET("food/preferences/get-food-sort")
        suspend fun getFoodSort(): Response<ApiResponseWithContent<FRes.FoodSortGetApiResponse>>

        @POST("food/preferences/update-food-sort")
        suspend fun updateFoodSort(
            @Body request: FReq.FoodSortUpdateRequest
        ): Response<ApiResponseWithContent<FRes.FoodSortUpdateApiResponse>>

        @GET("food/log/get-logs")
        suspend fun getFoodLogs(
            @Query("date") date: String
        ): Response<ApiResponseWithContent<FRes.FoodLogsGetApiResponse>>

        @POST("food/log/add")
        suspend fun addFoodLog(
            @Body request: FReq.FoodLogAddRequest
        ): Response<ApiResponseWithContent<FRes.FoodLogAddApiResponse>>

        @PUT("food/log/update")
        suspend fun updateFoodLog(
            @Body request: FReq.FoodLogUpdateRequest
        ): Response<ApiResponseWithContent<FRes.FoodLogUpdateApiResponse>>

        @DELETE("food/log/delete")
        suspend fun deleteFoodLog(
            @Query("food-log-id") foodLogId: Int
        ): Response<ApiResponseWithContent<FRes.FoodLogDeleteApiResponse>>
    }

    interface INutrientApiService {
        @GET("nutrient/get-nutrients")
        suspend fun getNutrients(): Response<ApiResponseWithContent<NRes.NutrientsByTypeGetApiResponse>>

        @GET("nutrient/get-intakes")
        suspend fun getNutrientIntakes(
            @Query("date") date: String
        ): Response<ApiResponseWithContent<NRes.NutrientIntakesByTypeGetApiResponse>>

        @POST("nutrient/set-goal")
        suspend fun setNutrientGoals(
            @Body request: NReq.NutrientGoalsPostRequest
        ): Response<ApiResponseWithContent<NRes.NutrientGoalsPostApiResponse>>

        @POST("nutrient/set-color")
        suspend fun setNutrientColors(
            @Body request: NReq.NutrientColorsPostRequest
        ): Response<ApiResponseWithContent<NRes.NutrientColorsApiPostResponse>>
    }
}