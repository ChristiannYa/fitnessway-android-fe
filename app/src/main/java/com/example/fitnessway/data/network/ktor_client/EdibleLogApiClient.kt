package com.example.fitnessway.data.network.ktor_client

import com.example.fitnessway.data.model.MFood
import com.example.fitnessway.data.model.m_26.EdibleLogAddRequest
import com.example.fitnessway.data.model.m_26.FoodLogAddResponse
import com.example.fitnessway.data.model.m_26.FoodLogUpdateRequest
import com.example.fitnessway.data.model.m_26.FoodLogUpdateResponse
import com.example.fitnessway.data.model.m_26.FoodLogsResponse
import com.example.fitnessway.data.network.ApiUrls
import com.example.fitnessway.util.extractData
import com.example.fitnessway.util.setJson
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.HttpClient as KtorHttpClient

class EdibleLogApiClient(private val client: KtorHttpClient) {
    suspend fun getByDate(date: String): FoodLogsResponse =
        client
            .get(ApiUrls.FoodLog.getListByDateUrl(date))
            .extractData()

    suspend fun add(req: EdibleLogAddRequest): FoodLogAddResponse =
        client
            .post(ApiUrls.FoodLog.ADD_URL) { setJson(req) }
            .extractData()

    suspend fun update(req: FoodLogUpdateRequest): FoodLogUpdateResponse =
        client
            .put(ApiUrls.FoodLog.UPDATE_URL) { setJson(req) }
            .extractData()

    suspend fun delete(foodLogId: Int): MFood.Api.Res.FoodLogDeleteApiResponse =
        client
            .delete(ApiUrls.FoodLog.DELETE_URL) {
                parameter("food-log-id", foodLogId)
            }
            .extractData()
}