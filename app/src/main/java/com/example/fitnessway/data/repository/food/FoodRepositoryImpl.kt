package com.example.fitnessway.data.repository.food

import com.example.fitnessway.data.model.food.FoodInformation
import com.example.fitnessway.data.model.food.FoodLogAddRequest
import com.example.fitnessway.data.model.food.FoodLogData
import com.example.fitnessway.data.model.food.FoodLogsByCategory
import com.example.fitnessway.data.network.Http
import com.example.fitnessway.data.network.food.IFoodApiService
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.flow.Flow

class FoodRepositoryImpl(
    private val apiService: IFoodApiService
) : IFoodRepository {
    override suspend fun getFoods(): Flow<UiState<List<FoodInformation>>> {
        return Http.makeRequest(
            apiCall = { apiService.getFoods() },
            extractData = { it.foods ?: emptyList() },
            errMsg = "Failed to get foods"
        )
    }

    override suspend fun getFoodLogs(date: String): Flow<UiState<FoodLogsByCategory>> {
        return Http.makeRequest(
            apiCall = { apiService.getFoodLogs(date) },
            extractData = { it.foodLogs },
            errMsg = "Failed to get food logs"
        )
    }

    override suspend fun addFoodLog(request: FoodLogAddRequest): Flow<UiState<FoodLogData>> {
        return Http.makeRequest(
            apiCall = { apiService.addFoodLog(request) },
            extractData = { it.foodLog },
            errMsg = "Failed to add food log"
        )
    }
}