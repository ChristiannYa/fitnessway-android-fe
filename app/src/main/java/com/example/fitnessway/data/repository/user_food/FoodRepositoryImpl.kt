package com.example.fitnessway.data.repository.user_food

import com.example.fitnessway.data.model.MFood.Api.Req.FoodAddRequest
import com.example.fitnessway.data.model.MFood.Api.Req.FoodFavoriteStatusUpdateRequest
import com.example.fitnessway.data.model.MFood.Api.Req.FoodLogUpdateRequest
import com.example.fitnessway.data.model.MFood.Api.Req.FoodSortUpdateRequest
import com.example.fitnessway.data.model.MFood.Api.Req.FoodUpdateRequest
import com.example.fitnessway.data.model.MFood.Model.FoodInformation
import com.example.fitnessway.data.model.MFood.Model.FoodLogData
import com.example.fitnessway.data.model.m_26.FoodLog
import com.example.fitnessway.data.model.m_26.FoodLogAddRequest
import com.example.fitnessway.data.model.m_26.FoodLogsCategorized
import com.example.fitnessway.data.network.ApiUrls
import com.example.fitnessway.data.network.HttpClient
import com.example.fitnessway.data.network.ktor_client.FoodApiClient
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FoodRepositoryImpl(
    private val httpClient: HttpClient,
    private val apiClient: FoodApiClient,
    private val repositoryScope: CoroutineScope,
) : IFoodRepository {

    private val _uiState = MutableStateFlow(FoodRepositoryUiState())
    override val uiState: StateFlow<FoodRepositoryUiState> = _uiState

    private fun fetchFoods(): Flow<UiState<List<FoodInformation>>> =
        httpClient.makeRequest(
            apiCall = apiClient::getFoods,
            extractData = { it.foods ?: emptyList() },
            errMsg = "Failed to get foods"
        )

    override fun refreshFoods() {
        _uiState.update { it.copy(foodsUiState = UiState.Loading) }

        repositoryScope.launch {
            fetchFoods().collect { state ->
                _uiState.update { it.copy(foodsUiState = state) }
            }
        }
    }

    override fun loadFoods() {
        val uiState = _uiState.value.foodsUiState
        if (uiState.hasState) return
        refreshFoods()
    }

    override suspend fun addFood(
        request: FoodAddRequest
    ): Flow<UiState<FoodInformation>> {
        return httpClient.makeRequest(
            apiCall = { apiClient.addFood(request) },
            extractData = { it.foodCreated },
            errMsg = "Failed to add food",
            invalidatedUrls = listOf(
                ApiUrls.Food.LIST_URL
            )
        ).onEach { state ->
            if (state is UiState.Success) {
                refreshFoods()
            }
        }
    }

    private var _updateFoodJob: Job? = null
    private val _updateFoodFlow = MutableSharedFlow<UiState<FoodInformation>>()

    override suspend fun updateFood(
        request: FoodUpdateRequest
    ): Flow<UiState<FoodInformation>> {
        _updateFoodJob?.cancel()

        _updateFoodJob = repositoryScope.launch {
            httpClient.makeRequest(
                apiCall = { apiClient.updateFood(request) },
                extractData = { it.foodUpdated },
                errMsg = "Failed to update food",
                invalidatedUrls = listOf(
                    ApiUrls.Food.LIST_URL,
                    ApiUrls.FoodLog.LIST_URL
                )
            ).collect { state ->
                _updateFoodFlow.emit(state)
            }
        }

        return _updateFoodFlow
    }

    override suspend fun deleteFood(
        foodId: Int
    ): Flow<UiState<FoodInformation>> {
        return httpClient.makeRequest(
            apiCall = { apiClient.deleteFood(foodId) },
            extractData = { it.foodDeleted },
            errMsg = "Failed to delete food",
            invalidatedUrls = listOf(
                ApiUrls.Food.LIST_URL,
                ApiUrls.FoodLog.LIST_URL
            )
        )
    }

    private var _updateFoodFavoriteStatusJob: Job? = null
    private val _updateFoodFavoriteStatusFlow = MutableSharedFlow<UiState<FoodInformation>>()

    override fun updateFoodFavoriteStatus(
        request: FoodFavoriteStatusUpdateRequest
    ): Flow<UiState<FoodInformation>> {
        _updateFoodFavoriteStatusJob?.cancel()

        _updateFoodFavoriteStatusJob = repositoryScope.launch {
            httpClient.makeRequest(
                apiCall = { apiClient.updateFoodFavoriteStatus(request) },
                extractData = { it.foodUpdated }
            ).collect { state ->
                _updateFoodFavoriteStatusFlow.emit(state)
            }
        }

        return _updateFoodFavoriteStatusFlow
    }

    private fun fetchFoodSort(): Flow<UiState<String>> {
        return httpClient.makeRequest(
            apiCall = apiClient::getFoodSort,
            extractData = { it.foodSort },
            errMsg = "Failed to get food sort"
        )
    }

    override fun refreshFoodSort() {
        _uiState.update { it.copy(foodSortUiState = UiState.Loading) }

        repositoryScope.launch {
            fetchFoodSort().collect { state ->
                _uiState.update { it.copy(foodSortUiState = state) }
            }
        }
    }

    override fun loadFoodSort() {
        val uiState = _uiState.value.foodSortUiState
        if (uiState.hasState) return
        refreshFoodSort()
    }

    override fun updateFoodSort(
        request: FoodSortUpdateRequest
    ): Flow<UiState<String>> {
        return httpClient.makeRequest(
            apiCall = { apiClient.updateFoodSort(request) },
            extractData = { it.foodSort },
            errMsg = "Failed to update food sort",
            invalidatedUrls = listOf(
                ApiUrls.Food.SORT_GET_URL,
                ApiUrls.Food.LIST_URL
            )
        )
    }

    private fun fetchFoodLogs(date: String): Flow<UiState<FoodLogsCategorized>> {
        return httpClient.makeRequest(
            apiCall = { apiClient.getFoodLogs(date) },
            extractData = { it.foodLogs },
            errMsg = "Failed to get logs"
        )
    }

    override fun clearFoodLogsUiCache() {
        _uiState.update { it.copy(foodLogsUiState = emptyMap()) }
    }

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
    ): Flow<UiState<FoodLogData>> {
        return httpClient.makeRequest(
            apiCall = { apiClient.updateFoodLog(request) },
            extractData = { it.updatedFoodLog },
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

    override fun updateState(update: (FoodRepositoryUiState) -> FoodRepositoryUiState) {
        _uiState.update(update)
    }

    override fun clearRepository() {
        _uiState.update { FoodRepositoryUiState() }
    }
}