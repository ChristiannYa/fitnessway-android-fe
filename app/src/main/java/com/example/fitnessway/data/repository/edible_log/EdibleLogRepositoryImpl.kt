package com.example.fitnessway.data.repository.edible_log

import com.example.fitnessway.data.model.m_26.EdibleLogAddRequest
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

    private val _date = MutableStateFlow("")

    override fun setDate(date: String) {
        _date.value = date
    }

    private fun fetchFoodLogs(date: String): Flow<UiState<FoodLogsCategorized>> =
        httpClient.makeRequest(
            apiCall = { apiClient.getByDate(date) },
            extractData = { it.foodLogs },
            errMsg = "Failed to get logs",
            pathDescription = "food log list"
        )

    override fun refresh() {
        val dateValue = _date.value

        repositoryScope.launch {
            fetchFoodLogs(dateValue).collect { state ->
                _uiState.update { it.copy(foodLogs = it.foodLogs + (dateValue to state)) }
            }
        }
    }

    override fun load() {
        val uiState = _uiState.value.foodLogs[_date.value]
        uiState?.let { if (it.hasResult) return }
        refresh()
    }

    override suspend fun add(
        request: EdibleLogAddRequest,
        date: String
    ): Flow<UiState<Unit>> =
        httpClient.makeRequest(
            apiCall = { apiClient.add(request) },
            extractData = { Unit },
            errMsg = "Failed to add log",
            pathDescription = "add food log"
        )

    override suspend fun update(
        request: FoodLogUpdateRequest,
        date: String
    ): Flow<UiState<Unit>> =
        httpClient.makeRequest(
            apiCall = { apiClient.update(request) },
            extractData = { Unit },
            errMsg = "Failed to update log",
            pathDescription = "update food log"
        )

    override suspend fun delete(
        foodLogId: Int,
        date: String
    ): Flow<UiState<Unit>> =
        httpClient.makeRequest(
            apiCall = { apiClient.delete(foodLogId) },
            extractData = { Unit },
            errMsg = "Failed to delete log",
            pathDescription = "delete food log"
        )

    override fun update(update: (EdibleLogRepositoryUiState) -> EdibleLogRepositoryUiState) =
        _uiState.update(update)

    override fun clear() =
        _uiState.update { EdibleLogRepositoryUiState() }
}
