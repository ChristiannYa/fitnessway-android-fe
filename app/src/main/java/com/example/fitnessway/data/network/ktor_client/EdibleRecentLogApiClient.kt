package com.example.fitnessway.data.network.ktor_client

import com.example.fitnessway.data.model.m_26.EdibleType
import com.example.fitnessway.data.model.m_26.PaginationParams
import com.example.fitnessway.data.model.m_26.RecentlyLoggedEdiblesResponse
import com.example.fitnessway.data.network.ApiUrls
import com.example.fitnessway.util.extractApiData
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.HttpClient as KtorHttpClient


class EdibleRecentLogApiClient(private val client: KtorHttpClient) {

    suspend fun getLatestLoggedEdibles(
        params: PaginationParams,
        edibleType: EdibleType
    ): RecentlyLoggedEdiblesResponse =
        client
            .get("${ApiUrls.FoodLog.LIST_LATEST_FOODS_URL}/${edibleType.name.lowercase()}") {
                parameter("limit", params.limit)
                parameter("offset", params.offset)
            }
            .extractApiData()

}