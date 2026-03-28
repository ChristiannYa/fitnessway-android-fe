package com.example.fitnessway.data.network.ktor_client

import com.example.fitnessway.data.model.MFood
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
    suspend fun getFoods(): MFood.Api.Res.FoodsGetApiResponse =
        client.get(ApiUrls.Food.LIST_URL).extractApiData()

    suspend fun addFood(
        req: MFood.Api.Req.FoodAddRequest
    ): MFood.Api.Res.FoodAddApiResponse =
        client.post(ApiUrls.Food.ADD_URL) {
            jsonBody(req)
        }.extractApiData()

    suspend fun updateFood(
        req: MFood.Api.Req.FoodUpdateRequest
    ): MFood.Api.Res.FoodUpdateApiResponse =
        client.put(ApiUrls.Food.UPDATE_URL) {
            jsonBody(req)
        }.extractApiData()

    suspend fun deleteFood(foodId: Int): MFood.Api.Res.FoodDeleteApiResponse =
        client.delete(ApiUrls.Food.DELETE_URL) {
            parameter("food-id", foodId)
        }.extractApiData()

    suspend fun updateFoodFavoriteStatus(
        req: MFood.Api.Req.FoodFavoriteStatusUpdateRequest
    ): MFood.Api.Res.FoodFavoriteStatusUpdateApiResponse =
        client.put(ApiUrls.Food.FAVORITE_STATUS_UPDATE_URL) {
            jsonBody(req)
        }.extractApiData()

    suspend fun getFoodSort(): MFood.Api.Res.FoodSortGetApiResponse =
        client.get(ApiUrls.Food.SORT_GET_URL).extractApiData()

    suspend fun updateFoodSort(
        req: MFood.Api.Req.FoodSortUpdateRequest
    ): MFood.Api.Res.FoodSortUpdateApiResponse =
        client.post(ApiUrls.Food.SORT_UPDATE_URL) {
            jsonBody(req)
        }.extractApiData()

    suspend fun getFoodLogs(date: String): MFood.Api.Res.FoodLogsGetApiResponse =
        client.get(ApiUrls.FoodLog.LIST_URL) {
            parameter("date", date)
        }.extractApiData()

    suspend fun addFoodLog(
        req: MFood.Api.Req.FoodLogAddRequest
    ): MFood.Api.Res.FoodLogAddApiResponse =
        client.post(ApiUrls.FoodLog.ADD_URL) {
            jsonBody(req)
        }.extractApiData()

    suspend fun updateFoodLog(
        req: MFood.Api.Req.FoodLogUpdateRequest
    ): MFood.Api.Res.FoodLogUpdateApiResponse =
        client.put(ApiUrls.FoodLog.UPDATE_URL) {
            jsonBody(req)
        }.extractApiData()

    suspend fun deleteFoodLog(foodLogId: Int): MFood.Api.Res.FoodLogDeleteApiResponse =
        client.delete(ApiUrls.FoodLog.DELETE_URL) {
            parameter("food-log-id", foodLogId)
        }.extractApiData()
}