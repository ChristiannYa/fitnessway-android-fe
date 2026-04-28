package com.example.fitnessway.data.network.ktor_client

import com.example.fitnessway.data.model.MFood
import com.example.fitnessway.data.model.m_26.EdibleAddRequest
import com.example.fitnessway.data.model.m_26.UserEdiblesGetResponse
import com.example.fitnessway.data.network.ApiUrls
import com.example.fitnessway.util.extractData
import com.example.fitnessway.util.setJson
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import kotlinx.coroutines.delay
import io.ktor.client.HttpClient as KtorHttpClient


class UserEdibleApiClient(private val client: KtorHttpClient) {
    suspend fun getList(
        limit: Int,
        offset: Long,
        edibleType: String
    ): UserEdiblesGetResponse =
        client
            .get("${ApiUrls.UserEdible.LIST_URL_KT}/$edibleType") {
                parameter("limit", limit)
                parameter("offset", offset)
            }
            .extractData()

    suspend fun add(req: EdibleAddRequest) = client
        .post("${ApiUrls.BASE_URL_KT}${ApiUrls.UserEdible.PATH_KT}") { setJson(req) }

    suspend fun update(
        req: MFood.Api.Req.FoodUpdateRequest
    ): MFood.Api.Res.FoodUpdateApiResponse {
        delay(16000)
        return client
            .put(ApiUrls.UserEdible.UPDATE_URL) { setJson(req) }
            .extractData()
    }

    suspend fun delete(foodId: Int): MFood.Api.Res.FoodDeleteApiResponse =
        client
            .delete(ApiUrls.UserEdible.DELETE_URL) {
                parameter("food-id", foodId)
            }
            .extractData()
}