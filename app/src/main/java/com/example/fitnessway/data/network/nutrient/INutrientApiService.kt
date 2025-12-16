package com.example.fitnessway.data.network.nutrient

import com.example.fitnessway.data.model.nutrient.NutrientColorsPostRequest
import com.example.fitnessway.data.model.nutrient.NutrientColorsPostResponse
import com.example.fitnessway.data.model.nutrient.NutrientGoalsPostRequest
import com.example.fitnessway.data.model.nutrient.NutrientGoalsPostResponse
import com.example.fitnessway.data.model.nutrient.NutrientIntakesByTypeFetchResponse
import com.example.fitnessway.data.model.nutrient.NutrientsByTypeFetchResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface INutrientApiService {
    @GET("nutrient/get-nutrients")
    suspend fun getNutrients(): Response<NutrientsByTypeFetchResponse>

    @GET("nutrient/get-intakes")
    suspend fun getNutrientIntakes(
        @Query("date") date: String
    ): Response<NutrientIntakesByTypeFetchResponse>

    @POST("nutrient/set-goal")
    suspend fun setNutrientGoals(
        @Body request: NutrientGoalsPostRequest
    ): Response<NutrientGoalsPostResponse>

    @POST("nutrient/set-color")
    suspend fun setNutrientColors(
        @Body request: NutrientColorsPostRequest
    ): Response<NutrientColorsPostResponse>
}