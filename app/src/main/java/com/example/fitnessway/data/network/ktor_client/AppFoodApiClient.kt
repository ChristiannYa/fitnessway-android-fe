package com.example.fitnessway.data.network.ktor_client

import com.example.fitnessway.data.model.m_26.AppFoodSearchResponse
import com.example.fitnessway.data.model.m_26.PaginationParams
import com.example.fitnessway.data.network.ApiUrls
import com.example.fitnessway.util.extractApiData
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.HttpClient as KtorHttpClient

class AppFoodApiClient(private val client: KtorHttpClient) {
    suspend fun searchAppFoods(
        query: String,
        params: PaginationParams
    ): AppFoodSearchResponse = client.get(ApiUrls.AppFood.LIST_URL) {
        parameter("limit", params.limit)
        parameter("offset", params.offset)
        parameter("q", query)
    }.extractApiData()
}