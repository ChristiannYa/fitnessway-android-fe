package com.example.fitnessway.data.network.ktor_client

import com.example.fitnessway.data.model.m_26.EdibleType
import com.example.fitnessway.data.model.m_26.UserEdiblesGetResponse
import com.example.fitnessway.data.network.ApiUrls
import com.example.fitnessway.util.extractApiData
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.HttpClient as KtorHttpClient

class UserSupplementApiClient(private val client: KtorHttpClient) {

    suspend fun get(
        limit: Int,
        offset: Long
    ): UserEdiblesGetResponse =
        client
            .get("${ApiUrls.UserEdible.LIST_URL_KT}/${EdibleType.SUPPLEMENT.name.lowercase()}") {
                parameter("limit", limit)
                parameter("offset", offset)
            }
            .extractApiData()

}