package com.example.fitnessway.data.network.nutrient

import com.example.fitnessway.data.model.MNutrient.Api.Req.NutrientColorsPostRequest
import com.example.fitnessway.data.model.MNutrient.Api.Req.NutrientGoalsPostRequest
import com.example.fitnessway.data.model.MNutrient.Api.Res.NutrientColorsApiPostResponse
import com.example.fitnessway.data.model.MNutrient.Api.Res.NutrientGoalsPostApiResponse
import com.example.fitnessway.data.model.MNutrient.Api.Res.NutrientIntakesByTypeGetApiResponse
import com.example.fitnessway.data.model.MNutrient.Api.Res.NutrientsByTypeGetApiResponse
import com.example.fitnessway.data.model.MApi.Model.ApiResponseWithContent
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface INutrientApiService {
    @GET("nutrient/get-nutrients")
    suspend fun getNutrients(): Response<ApiResponseWithContent<NutrientsByTypeGetApiResponse>>

    @GET("nutrient/get-intakes")
    suspend fun getNutrientIntakes(
        @Query("date") date: String
    ): Response<ApiResponseWithContent<NutrientIntakesByTypeGetApiResponse>>

    @POST("nutrient/set-goal")
    suspend fun setNutrientGoals(
        @Body request: NutrientGoalsPostRequest
    ): Response<NutrientGoalsPostApiResponse>

    @POST("nutrient/set-color")
    suspend fun setNutrientColors(
        @Body request: NutrientColorsPostRequest
    ): Response<ApiResponseWithContent<NutrientColorsApiPostResponse>>
}