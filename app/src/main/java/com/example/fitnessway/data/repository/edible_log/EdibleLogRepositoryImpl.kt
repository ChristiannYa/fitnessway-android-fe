package com.example.fitnessway.data.repository.edible_log

import com.example.fitnessway.data.model.MFood.Model.FoodLogData
import com.example.fitnessway.data.model.m_26.EdibleLogAddRequest
import com.example.fitnessway.data.model.m_26.FoodLog
import com.example.fitnessway.data.model.m_26.FoodLogUpdateRequest
import com.example.fitnessway.data.model.m_26.FoodLogsCategorized
import com.example.fitnessway.data.network.HttpClient
import com.example.fitnessway.data.network.ktor_client.EdibleLogApiClient
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EdibleLogRepositoryImpl(
    private val httpClient: HttpClient,
    private val apiClient: EdibleLogApiClient,
    private val repositoryScope: CoroutineScope
) : IEdibleLogRepository {

    private val _uiState = MutableStateFlow(EdibleLogRepositoryUiState())
    override val uiState: StateFlow<EdibleLogRepositoryUiState> = _uiState

    private fun fetchFoodLogs(date: String): Flow<UiState<FoodLogsCategorized>> =
        httpClient.makeRequest(
            apiCall = { apiClient.getByDate(date) },
            extractData = { it.foodLogs },
            errMsg = "Failed to get logs",
            pathDescription = "food log list"
        )

    override fun refresh(date: String) {
        repositoryScope.launch {
            fetchFoodLogs(date).collect { state ->
                _uiState.update { it.copy(foodLogs = it.foodLogs + (date to state)) }
            }
        }
    }

    override fun load(date: String) {
        val uiState = _uiState.value.foodLogs[date]
        uiState?.let { if (it.hasState) return }
        refresh(date)
    }

    override suspend fun add(
        request: EdibleLogAddRequest,
        date: String
    ): Flow<UiState<FoodLog>> =
        httpClient.makeRequest(
            apiCall = { apiClient.add(request) },
            extractData = { it.foodLogAdded },
            errMsg = "Failed to add log",
            pathDescription = "add food log"
        )

    override suspend fun update(
        request: FoodLogUpdateRequest,
        date: String
    ): Flow<UiState<FoodLog>> =
        httpClient.makeRequest(
            apiCall = { apiClient.update(request) },
            extractData = { it.foodLogUpdated },
            errMsg = "Failed to update log",
            pathDescription = "update food log"
        )

    override suspend fun delete(
        foodLogId: Int,
        date: String
    ): Flow<UiState<FoodLogData>> =
        httpClient.makeRequest(
            apiCall = { apiClient.delete(foodLogId) },
            extractData = { it.foodLogDeleted },
            errMsg = "Failed to delete log",
            pathDescription = "delete food log"
        )

    override fun clearMap() = _uiState.update { it.copy(foodLogs = emptyMap()) }

    override fun updateState(update: (EdibleLogRepositoryUiState) -> EdibleLogRepositoryUiState) =
        _uiState.update(update)

    override fun clear() = _uiState.update { EdibleLogRepositoryUiState() }
}
