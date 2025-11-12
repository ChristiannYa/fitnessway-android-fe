package com.example.fitnessway.data.repository.food

import com.example.fitnessway.data.model.food.FoodInformation
import com.example.fitnessway.data.model.food.FoodLogAddRequest
import com.example.fitnessway.data.model.food.FoodLogData
import com.example.fitnessway.data.model.food.FoodLogsByCategory
import com.example.fitnessway.data.network.ApiUrls
import com.example.fitnessway.data.network.HttpClient
import com.example.fitnessway.data.network.food.IFoodApiService
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.flow.Flow

class FoodRepositoryImpl(
    private val apiService: IFoodApiService,
    private val httpClient: HttpClient
) : IFoodRepository {
    override suspend fun getFoods(): Flow<UiState<List<FoodInformation>>> {
        return httpClient.makeRequest(
            apiCall = { apiService.getFoods() },
            extractData = { it.foods ?: emptyList() },
            errMsg = "Failed to get foods"
        )
    }

    override suspend fun getFoodLogs(date: String): Flow<UiState<FoodLogsByCategory>> {
        return httpClient.makeRequest(
            apiCall = { apiService.getFoodLogs(date) },
            extractData = { it.foodLogs },
            errMsg = "Failed to get food logs"
        )
    }

    override suspend fun addFoodLog(
        request: FoodLogAddRequest,
        date: String
    ): Flow<UiState<FoodLogData>> {
        return httpClient.makeRequest(
            apiCall = { apiService.addFoodLog(request) },
            extractData = { it.foodLogAdded },
            errMsg = "Failed to add food log",
            invalidatedUrls = listOf(
                ApiUrls.Nutrient.getIntakes(date),
                ApiUrls.Food.getLogs(date)
            )
        )
    }

    override suspend fun deleteFoodLog(
        foodLogId: Int,
        date: String
    ): Flow<UiState<FoodLogData>> {
        return httpClient.makeRequest(
            apiCall = { apiService.deleteFoodLog(foodLogId) },
            extractData = { it.foodLogDeleted },
            errMsg = "Failed to delete food log",
            invalidatedUrls = listOf(
                ApiUrls.Nutrient.getIntakes(date),
                ApiUrls.Food.getLogs(date)
            )
        )
    }
}