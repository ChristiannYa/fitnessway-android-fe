package com.example.fitnessway.data.repository.food_log

import com.example.fitnessway.data.model.MFood.Model.FoodLogData
import com.example.fitnessway.data.model.m_26.FoodLog
import com.example.fitnessway.data.model.m_26.FoodLogAddRequest
import com.example.fitnessway.data.model.m_26.FoodLogUpdateRequest
import com.example.fitnessway.data.model.m_26.FoodLogsCategorized
import com.example.fitnessway.data.network.ApiUrls
import com.example.fitnessway.data.network.HttpClient
import com.example.fitnessway.data.network.ktor_client.FoodLogApiClient
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FoodLogRepositoryImpl(
    private val httpClient: HttpClient,
    private val apiClient: FoodLogApiClient,
    private val repositoryScope: CoroutineScope
) : IFoodLogRepository {

    private val _uiState = MutableStateFlow(FoodLogRepositoryUiState())
    override val uiState: StateFlow<FoodLogRepositoryUiState> = _uiState

    private fun fetchFoodLogs(date: String): Flow<UiState<FoodLogsCategorized>> =
        httpClient.makeRequest(
            apiCall = { apiClient.getFoodLogs(date) },
            extractData = { it.foodLogs },
            errMsg = "Failed to get logs"
        )

    override fun refreshFoodLogs(date: String) {
        repositoryScope.launch {
            fetchFoodLogs(date).collect { state ->
                _uiState.update { it.copy(foodLogsUiState = it.foodLogsUiState + (date to state)) }
            }
        }
    }

    override fun loadFoodLogs(date: String) {
        val uiState = _uiState.value.foodLogsUiState[date]
        uiState?.let { if (it.hasState) return }

        refreshFoodLogs(date)
    }

    override suspend fun addFoodLog(
        request: FoodLogAddRequest,
        date: String
    ): Flow<UiState<FoodLog>> {
        return httpClient.makeRequest(
            apiCall = { apiClient.addFoodLog(request) },
            extractData = { it.foodLogAdded },
            errMsg = "Failed to add log",
            invalidatedUrls = listOf(
                ApiUrls.Nutrient.getIntakesByDateUrl(date),
                ApiUrls.FoodLog.getListByDateUrl(date)
            )
        )
    }

    override suspend fun updateFoodLog(
        request: FoodLogUpdateRequest,
        date: String
    ): Flow<UiState<FoodLog>> {
        return httpClient.makeRequest(
            apiCall = { apiClient.updateFoodLog(request) },
            extractData = { it.foodLogUpdated },
            errMsg = "Failed to update log",
            invalidatedUrls = listOf(
                ApiUrls.FoodLog.getListByDateUrl(date),
                ApiUrls.Nutrient.getIntakesByDateUrl(date)
            )
        )
    }

    override suspend fun deleteFoodLog(
        foodLogId: Int,
        date: String
    ): Flow<UiState<FoodLogData>> {
        return httpClient.makeRequest(
            apiCall = { apiClient.deleteFoodLog(foodLogId) },
            extractData = { it.foodLogDeleted },
            errMsg = "Failed to delete log",
            invalidatedUrls = listOf(
                ApiUrls.Nutrient.getIntakesByDateUrl(date),
                ApiUrls.FoodLog.getListByDateUrl(date)
            )
        )
    }

    override fun clearFoodLogsUiCache() = _uiState.update { it.copy(foodLogsUiState = emptyMap()) }

    override fun clearRepository() = _uiState.update { FoodLogRepositoryUiState() }
}
