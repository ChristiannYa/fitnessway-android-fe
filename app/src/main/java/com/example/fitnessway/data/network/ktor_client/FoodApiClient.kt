package com.example.fitnessway.data.network.ktor_client

import com.example.fitnessway.data.model.MFood
import com.example.fitnessway.data.model.m_26.UserEdiblesGetResponse
import com.example.fitnessway.data.network.ApiUrls
import com.example.fitnessway.util.extractApiData
import com.example.fitnessway.util.jsonBody
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.HttpClient as KtorHttpClient


class FoodApiClient(private val client: KtorHttpClient) {
    suspend fun getEdibles(
        limit: Int,
        offset: Long,
        edibleType: String
    ): UserEdiblesGetResponse =
        client
            .get("${ApiUrls.UserEdible.LIST_URL_KT}/$edibleType") {
                parameter("limit", limit)
                parameter("offset", offset)
            }
            .extractApiData()

    suspend fun addFood(
        req: MFood.Api.Req.FoodAddRequest
    ): MFood.Api.Res.FoodAddApiResponse =
        client
            .post(ApiUrls.UserEdible.ADD_URL) { jsonBody(req) }
            .extractApiData()

    suspend fun updateFood(
        req: MFood.Api.Req.FoodUpdateRequest
    ): MFood.Api.Res.FoodUpdateApiResponse =
        client
            .put(ApiUrls.UserEdible.UPDATE_URL) { jsonBody(req) }
            .extractApiData()

    suspend fun deleteFood(foodId: Int): MFood.Api.Res.FoodDeleteApiResponse =
        client
            .delete(ApiUrls.UserEdible.DELETE_URL) { parameter("food-id", foodId) }
            .extractApiData()
}