package com.example.fitnessway.data.network.ktor_client

import com.example.fitnessway.data.model.MNutrient
import com.example.fitnessway.data.network.ApiUrls
import com.example.fitnessway.util.extractApiData
import com.example.fitnessway.util.jsonBody
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.HttpClient as KtorHttpClient


class NutrientApiClient(private val client: KtorHttpClient) {
    suspend fun getNutrients(): MNutrient.Api.Res.NutrientsByTypeGetApiResponse =
        client.get(ApiUrls.Nutrient.NUTRIENT_LIST_URL).extractApiData()

    suspend fun getNutrientIntakes(
        date: String
    ): MNutrient.Api.Res.NutrientIntakesByTypeGetApiResponse =
        client.get(ApiUrls.Nutrient.NUTRIENT_INTAKES_URL) {
            parameter("date", date)
        }.extractApiData()

    suspend fun setNutrientGoals(
        req: MNutrient.Api.Req.NutrientGoalsPostRequest
    ): MNutrient.Api.Res.NutrientGoalsPostApiResponse =
        client.post(ApiUrls.Nutrient.NUTRIENT_GOAL_SET_URL) {
            jsonBody(req)
        }.extractApiData()

    suspend fun setNutrientColors(
        req: MNutrient.Api.Req.NutrientColorsPostRequest
    ): MNutrient.Api.Res.NutrientColorsApiPostResponse =
        client.post(ApiUrls.Nutrient.NUTRIENT_COLOR_SET_URL) {
            jsonBody(req)
        }.extractApiData()
}