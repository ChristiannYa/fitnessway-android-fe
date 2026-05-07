package com.example.fitnessway.data.network.ktor_client

import com.example.fitnessway.data.model.m_26.NutrientIntakesResponse
import com.example.fitnessway.data.network.ApiUrls
import com.example.fitnessway.util.extractData
import io.ktor.client.request.get
import io.ktor.client.HttpClient as KtorHttpClient

class NutrientIntakeApiClient(private val client: KtorHttpClient) {

    suspend fun get(date: String): NutrientIntakesResponse =
        client
            .get(ApiUrls.Nutrient.getIntakesByDateUrl(date))
            .extractData()
}