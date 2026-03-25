package com.example.fitnessway.data.network.ktor_client

import com.example.fitnessway.data.model.MFood
import com.example.fitnessway.data.model.m_26.PendingFoodAddRequest
import com.example.fitnessway.data.model.m_26.PendingFoodAddResponse
import com.example.fitnessway.data.model.m_26.PendingFoodsGetResponse
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
    // @TODO: Move into its dedicated `PendingFoodApiClient` scope
    suspend fun getPendingFoods(
        limit: Int,
        offset: Long
    ): PendingFoodsGetResponse = client.get(ApiUrls.Food.FOOD_PENDING_LIST_URL) {
        parameter("limit", limit)
        parameter("offset", offset)
    }.extractApiData()


    suspend fun addPendingFood(
        req: PendingFoodAddRequest
    ): PendingFoodAddResponse = client.post(ApiUrls.Food.FOOD_PENDING_ADD_URL) {
        jsonBody(req)
    }.extractApiData()

    suspend fun getFoods(): MFood.Api.Res.FoodsGetApiResponse =
        client.get(ApiUrls.Food.FOOD_USER_LIST_URL).extractApiData()

    suspend fun addFood(
        req: MFood.Api.Req.FoodAddRequest
    ): MFood.Api.Res.FoodAddApiResponse =
        client.post(ApiUrls.Food.FOOD_USER_ADD_URL) {
            jsonBody(req)
        }.extractApiData()

    suspend fun updateFood(
        req: MFood.Api.Req.FoodUpdateRequest
    ): MFood.Api.Res.FoodUpdateApiResponse =
        client.post(ApiUrls.Food.FOOD_USER_UPDATE_URL) {
            jsonBody(req)
        }.extractApiData()

    suspend fun deleteFood(foodId: Int): MFood.Api.Res.FoodDeleteApiResponse =
        client.delete(ApiUrls.Food.FOOD_USER_DELETE_URL) {
            parameter("food-id", foodId)
        }.extractApiData()

    suspend fun updateFoodFavoriteStatus(
        req: MFood.Api.Req.FoodFavoriteStatusUpdateRequest
    ): MFood.Api.Res.FoodFavoriteStatusUpdateApiResponse =
        client.put(ApiUrls.Food.FOOD_USER_FAVORITE_STATUS_UPDATE_URL) {
            jsonBody(req)
        }.extractApiData()

    suspend fun getFoodSort(): MFood.Api.Res.FoodSortGetApiResponse =
        client.get(ApiUrls.Food.FOOD_USER_SORT_GET_URL).extractApiData()

    suspend fun updateFoodSort(
        req: MFood.Api.Req.FoodSortUpdateRequest
    ): MFood.Api.Res.FoodSortUpdateApiResponse =
        client.post(ApiUrls.Food.FOOD_USER_SORT_UPDATE_URL) {
            jsonBody(req)
        }.extractApiData()

    suspend fun getFoodLogs(date: String): MFood.Api.Res.FoodLogsGetApiResponse =
        client.get(ApiUrls.Food.FOOD_LOG_LIST_URL) {
            parameter("date", date)
        }.extractApiData()

    suspend fun addFoodLog(
        req: MFood.Api.Req.FoodLogAddRequest
    ): MFood.Api.Res.FoodLogAddApiResponse =
        client.post(ApiUrls.Food.FOOD_LOG_ADD_URL) {
            jsonBody(req)
        }.extractApiData()

    suspend fun updateFoodLog(
        req: MFood.Api.Req.FoodLogUpdateRequest
    ): MFood.Api.Res.FoodLogUpdateApiResponse =
        client.put(ApiUrls.Food.FOOD_LOG_UPDATE_URL) {
            jsonBody(req)
        }.extractApiData()

    suspend fun deleteFoodLog(foodLogId: Int): MFood.Api.Res.FoodLogDeleteApiResponse =
        client.delete(ApiUrls.Food.FOOD_LOG_DELETE_URL) {
            parameter("food-log-id", foodLogId)
        }.extractApiData()
}