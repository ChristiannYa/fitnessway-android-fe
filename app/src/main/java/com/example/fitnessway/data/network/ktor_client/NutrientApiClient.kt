package com.example.fitnessway.data.network.ktor_client

import com.example.fitnessway.data.model.MNutrient
import com.example.fitnessway.data.model.m_26.NutrientIntakesResponse
import com.example.fitnessway.data.network.ApiUrls
import com.example.fitnessway.util.extractData
import com.example.fitnessway.util.setJson
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.HttpClient as KtorHttpClient


class NutrientApiClient(private val client: KtorHttpClient) {
    suspend fun getNutrients(): MNutrient.Api.Res.NutrientsByTypeGetApiResponse =
        client.get(ApiUrls.Nutrient.NUTRIENT_LIST_URL).extractData()

    suspend fun getNutrientIntakes(date: String): NutrientIntakesResponse =
        client
            .get(ApiUrls.Nutrient.getIntakesByDateUrl(date))
            .extractData()

    suspend fun setNutrientGoals(
        req: MNutrient.Api.Req.NutrientGoalsPostRequest
    ): MNutrient.Api.Res.NutrientGoalsPostApiResponse =
        client.post(ApiUrls.Nutrient.NUTRIENT_GOAL_SET_URL) {
            setJson(req)
        }.extractData()

    suspend fun setNutrientColors(
        req: MNutrient.Api.Req.NutrientColorsPostRequest
    ): MNutrient.Api.Res.NutrientColorsApiPostResponse =
        client.post(ApiUrls.Nutrient.NUTRIENT_COLOR_SET_URL) {
            setJson(req)
        }.extractData()
}