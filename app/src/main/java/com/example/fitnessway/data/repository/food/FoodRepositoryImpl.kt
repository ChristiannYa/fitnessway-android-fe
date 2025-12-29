package com.example.fitnessway.data.repository.food

import com.example.fitnessway.data.model.food.FoodAddRequest
import com.example.fitnessway.data.model.food.FoodInformation
import com.example.fitnessway.data.model.food.FoodLogAddRequest
import com.example.fitnessway.data.model.food.FoodLogData
import com.example.fitnessway.data.model.food.FoodLogUpdateRequest
import com.example.fitnessway.data.model.food.FoodLogsByCategory
import com.example.fitnessway.data.model.food.FoodUpdateRequest
import com.example.fitnessway.data.network.ApiUrls
import com.example.fitnessway.data.network.CacheManager
import com.example.fitnessway.data.network.HttpClient
import com.example.fitnessway.data.network.food.IFoodApiService
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FoodRepositoryImpl(
    private val apiService: IFoodApiService,
    private val httpClient: HttpClient,
    private val repositoryScope: CoroutineScope,
    private val cacheManager: CacheManager
) : IFoodRepository {

    private val _uiState = MutableStateFlow(FoodRepositoryUiState())
    override val uiState: StateFlow<FoodRepositoryUiState> = _uiState

    private fun fetchFoods(): Flow<UiState<List<FoodInformation>>> {
        return httpClient.makeRequest(
            apiCall = { apiService.getFoods() },
            extractData = { it.foods ?: emptyList() },
            errMsg = "Failed to get foods"
        )
    }

    override fun refreshFoods() {
        cacheManager.evictUrl(ApiUrls.Food.FOODS)
        _uiState.update { it.copy(foodsUiState = UiState.Loading) }

        repositoryScope.launch {
            fetchFoods().collect { state ->
                _uiState.update { it.copy(foodsUiState = state) }
            }
        }
    }

    override fun loadFoods() {
        val foodsUiState = _uiState.value.foodsUiState
        if (foodsUiState is UiState.Success) return
        refreshFoods()
    }

    override suspend fun addFood(
        request: FoodAddRequest
    ): Flow<UiState<FoodInformation>> {
        return httpClient.makeRequest(
            apiCall = { apiService.addFood(request) },
            extractData = { it.foodCreated },
            errMsg = "Failed to add food",
            invalidatedUrls = listOf(
                ApiUrls.Food.FOODS
            )
        ).onEach { state ->
            if (state is UiState.Success) {
                refreshFoods()
            }
        }
    }

    // @TODO: Re-fetch food logs data when updating food data
    override suspend fun updateFood(
        request: FoodUpdateRequest
    ): Flow<UiState<FoodInformation>> {
        return httpClient.makeRequest(
            apiCall = { apiService.updateFood(request) },
            extractData = { it.updatedFood },
            errMsg = "Failed to update food",
            invalidatedUrls = listOf(
                ApiUrls.Food.FOODS,
                ApiUrls.Food.ALL_LOGS
            )
        )
    }

    // @TODO: Re-fetch food logs data when deleting the food, and make the request optimistic
    override suspend fun deleteFood(
        foodId: Int
    ): Flow<UiState<FoodInformation>> {
        return httpClient.makeRequest(
            apiCall = { apiService.deleteFood(foodId) },
            extractData = { it.foodDeleted },
            errMsg = "Failed to delete food",
            invalidatedUrls = listOf(
                ApiUrls.Food.FOODS,
                ApiUrls.Food.ALL_LOGS
            )
        )
    }

    private fun fetchFoodLogs(date: String): Flow<UiState<FoodLogsByCategory>> {
        return httpClient.makeRequest(
            apiCall = { apiService.getFoodLogs(date) },
            extractData = { it.foodLogs },
            errMsg = "Failed to get logs"
        )
    }

    override fun clearFoodLogsUiCache() {
        _uiState.update { it.copy(foodLogsCache = emptyMap()) }
    }

    override fun refreshFoodLogs(date: String) {
        cacheManager.evictUrl(ApiUrls.Food.getLogs(date))

        repositoryScope.launch {
            fetchFoodLogs(date).collect { state ->
                _uiState.update { it.copy(foodLogsCache = it.foodLogsCache + (date to state)) }
            }
        }
    }

    override fun loadFoodLogs(date: String) {
        val cachedData = _uiState.value.foodLogsCache[date]
        if (cachedData is UiState.Success) {
            return
        }
        refreshFoodLogs(date)
    }

    override suspend fun addFoodLog(
        request: FoodLogAddRequest,
        date: String
    ): Flow<UiState<FoodLogData>> {
        return httpClient.makeRequest(
            apiCall = { apiService.addFoodLog(request) },
            extractData = { it.foodLogAdded },
            errMsg = "Failed to add log",
            invalidatedUrls = listOf(
                ApiUrls.Nutrient.getIntakes(date),
                ApiUrls.Food.getLogs(date)
            )
        )
    }

    override suspend fun updateFoodLog(
        request: FoodLogUpdateRequest,
        date: String
    ): Flow<UiState<FoodLogData>> {
        return httpClient.makeRequest(
            apiCall = { apiService.updateFoodLog(request) },
            extractData = { it.updatedFoodLog },
            errMsg = "Failed to update log",
            invalidatedUrls = listOf(
                ApiUrls.Food.getLogs(date),
                ApiUrls.Nutrient.getIntakes(date)
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
            errMsg = "Failed to delete log",
            invalidatedUrls = listOf(
                ApiUrls.Nutrient.getIntakes(date),
                ApiUrls.Food.getLogs(date)
            )
        )
    }

    override fun updateState(update: (FoodRepositoryUiState) -> FoodRepositoryUiState) {
        _uiState.update(update)
    }
}