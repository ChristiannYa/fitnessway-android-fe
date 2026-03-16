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
        @POST(ApiUrls.Auth.LOGOUT_PATH)
        suspend fun logout(
            @Body request: AReq.LogoutRequest
        ): Response<ApiResponse>
    }

    interface IUnauthorized {
        @POST(ApiUrls.Auth.LOGIN_PATH)
        suspend fun login(
            @Body request: AReq.LoginRequest
        ): Response<ApiResponseWithContent<ARes.LoginApiResponse>>

        @POST(ApiUrls.Auth.REGISTER_PATH)
        suspend fun register(
            @Body request: AReq.RegisterRequest
        ): Response<ApiResponseWithContent<ARes.RegisterApiResponse>>
    }

    interface IUser {
        @GET(ApiUrls.User.USER_PATH)
        suspend fun getUser(): Response<ApiResponseWithContent<URes.UserApiGetResponse>>
    }

    interface IFoodApiService {
        @GET(ApiUrls.Food.FOOD_USER_LIST_PATH)
        suspend fun getFoods(): Response<ApiResponseWithContent<FRes.FoodsGetApiResponse>>

        @POST(ApiUrls.Food.FOOD_USER_ADD_PATH)
        suspend fun addFood(
            @Body request: FReq.FoodAddRequest
        ): Response<ApiResponseWithContent<FRes.FoodAddApiResponse>>

        @PUT(ApiUrls.Food.FOOD_USER_UPDATE_PATH)
        suspend fun updateFood(
            @Body request: FReq.FoodUpdateRequest
        ): Response<ApiResponseWithContent<FRes.FoodUpdateApiResponse>>

        @DELETE(ApiUrls.Food.FOOD_USER_DELETE_PATH)
        suspend fun deleteFood(
            @Query("food-id") foodId: Int
        ): Response<ApiResponseWithContent<FRes.FoodDeleteApiResponse>>

        @PUT(ApiUrls.Food.FOOD_USER_FAVORITE_STATUS_UPDATE_PATH)
        suspend fun updateFoodFavoriteStatus(
            @Body request: FReq.FoodFavoriteStatusUpdateRequest
        ): Response<ApiResponseWithContent<FRes.FoodFavoriteStatusUpdateApiResponse>>

        @GET(ApiUrls.Food.FOOD_USER_SORT_GET_PATH)
        suspend fun getFoodSort(): Response<ApiResponseWithContent<FRes.FoodSortGetApiResponse>>

        @POST(ApiUrls.Food.FOOD_USER_SORT_UPDATE_PATH)
        suspend fun updateFoodSort(
            @Body request: FReq.FoodSortUpdateRequest
        ): Response<ApiResponseWithContent<FRes.FoodSortUpdateApiResponse>>

        @GET(ApiUrls.Food.FOOD_LOG_LIST_PATH)
        suspend fun getFoodLogs(
            @Query("date") date: String
        ): Response<ApiResponseWithContent<FRes.FoodLogsGetApiResponse>>

        @POST(ApiUrls.Food.FOOD_LOG_ADD_PATH)
        suspend fun addFoodLog(
            @Body request: FReq.FoodLogAddRequest
        ): Response<ApiResponseWithContent<FRes.FoodLogAddApiResponse>>

        @PUT(ApiUrls.Food.FOOD_LOG_UPDATE_PATH)
        suspend fun updateFoodLog(
            @Body request: FReq.FoodLogUpdateRequest
        ): Response<ApiResponseWithContent<FRes.FoodLogUpdateApiResponse>>

        @DELETE(ApiUrls.Food.FOOD_LOG_DELETE_PATH)
        suspend fun deleteFoodLog(
            @Query("food-log-id") foodLogId: Int
        ): Response<ApiResponseWithContent<FRes.FoodLogDeleteApiResponse>>
    }

    interface INutrientApiService {
        @GET(ApiUrls.Nutrient.NUTRIENT_LIST_PATH)
        suspend fun getNutrients(): Response<ApiResponseWithContent<NRes.NutrientsByTypeGetApiResponse>>

        @GET(ApiUrls.Nutrient.NUTRIENT_INTAKES_PATH)
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