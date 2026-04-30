package com.example.fitnessway.data.network.ktor_client

import com.example.fitnessway.data.model.m_26.EdibleAddRequest
import com.example.fitnessway.data.model.m_26.PendingFoodsGetResponse
import com.example.fitnessway.data.network.ApiUrls
import com.example.fitnessway.util.extractData
import com.example.fitnessway.util.setJson
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.HttpClient as KtorHttpClient

class PendingEdibleApiClient(private val client: KtorHttpClient) {
    suspend fun getList(
        limit: Int,
        offset: Long,
        edibleType: String
    ): PendingFoodsGetResponse =
        client
            .get("${ApiUrls.PendingEdible.LIST_URL}/$edibleType") {
                parameter("limit", limit)
                parameter("offset", offset)
            }
            .extractData()

    suspend fun add(req: EdibleAddRequest) =
        client
            .post("${ApiUrls.BASE_URL_KT}${ApiUrls.PendingEdible.PATH}") {
                setJson(req)
            }

    suspend fun dismiss(id: Int) =
        client.delete("${ApiUrls.PendingEdible.DISMISS_REVIEW_URL}/$id")
}