package com.example.fitnessway.data.network.ktor_client

import com.example.fitnessway.data.model.m_26.PendingFoodAddRequest
import com.example.fitnessway.data.model.m_26.PendingFoodAddResponse
import com.example.fitnessway.data.model.m_26.PendingFoodsGetResponse
import com.example.fitnessway.data.network.ApiUrls
import com.example.fitnessway.util.extractData
import com.example.fitnessway.util.setJson
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.HttpClient as KtorHttpClient

class PendingFoodApiClient(private val client: KtorHttpClient) {
    suspend fun getPendingFoods(
        limit: Int,
        offset: Long
    ): PendingFoodsGetResponse =
        client
            .get(ApiUrls.PendingEdible.LIST_URL) {
                parameter("limit", limit)
                parameter("offset", offset)
            }
            .extractData()

    suspend fun addPendingFood(
        req: PendingFoodAddRequest
    ): PendingFoodAddResponse =
        client
            .post(ApiUrls.PendingEdible.ADD_URL) {
                setJson(req)
            }
            .extractData()

    suspend fun dismissReview(id: Int) =
        client.delete(ApiUrls.PendingEdible.getDismissReviewUrl(id))
}