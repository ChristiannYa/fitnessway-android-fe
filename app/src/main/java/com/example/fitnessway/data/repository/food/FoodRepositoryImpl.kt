package com.example.fitnessway.data.repository.food

import com.example.fitnessway.data.model.MFood.Api.Req.FoodAddRequest
import com.example.fitnessway.data.model.MFood.Api.Req.FoodFavoriteStatusUpdateRequest
import com.example.fitnessway.data.model.MFood.Api.Req.FoodLogAddRequest
import com.example.fitnessway.data.model.MFood.Api.Req.FoodLogUpdateRequest
import com.example.fitnessway.data.model.MFood.Api.Req.FoodSortUpdateRequest
import com.example.fitnessway.data.model.MFood.Api.Req.FoodUpdateRequest
import com.example.fitnessway.data.model.MFood.Model.FoodInformation
import com.example.fitnessway.data.model.MFood.Model.FoodLogData
import com.example.fitnessway.data.model.MFood.Model.FoodLogsByCategory
import com.example.fitnessway.data.network.ApiUrls
import com.example.fitnessway.data.network.CacheManager
import com.example.fitnessway.data.network.HttpClient
import com.example.fitnessway.data.network.RetrofitService.IFoodApiService
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
        cacheManager.evictUrl(ApiUrls.Food.FOOD_USER_LIST_URL)
        _uiState.update { it.copy(foodsUiState = UiState.Loading) }

        repositoryScope.launch {
            fetchFoods().collect { state ->
                _uiState.update { it.copy(foodsUiState = state) }
            }
        }
    }

    override fun loadFoods() {
        val foodsUiState = _uiState.value.foodsUiState
        if (foodsUiState is UiState.Success || foodsUiState is UiState.Error) return
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
                ApiUrls.Food.FOOD_USER_LIST_URL
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
                apiCall = { apiService.updateFood(request) },
                extractData = { it.foodUpdated },
                errMsg = "Failed to update food",
                invalidatedUrls = listOf(
                    ApiUrls.Food.FOOD_USER_LIST_URL,
                    ApiUrls.Food.FOOD_LOG_LIST_URL
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
            apiCall = { apiService.deleteFood(foodId) },
            extractData = { it.foodDeleted },
            errMsg = "Failed to delete food",
            invalidatedUrls = listOf(
                ApiUrls.Food.FOOD_USER_LIST_URL,
                ApiUrls.Food.FOOD_LOG_LIST_URL
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
                apiCall = { apiService.updateFoodFavoriteStatus(request) },
                extractData = { it.foodUpdated }
            ).collect { state ->
                _updateFoodFavoriteStatusFlow.emit(state)
            }
        }

        return _updateFoodFavoriteStatusFlow
    }

    private fun fetchFoodSort(): Flow<UiState<String>> {
        return httpClient.makeRequest(
            apiCall = { apiService.getFoodSort() },
            extractData = { it.foodSort },
            errMsg = "Failed to get food sort"
        )
    }

    override fun refreshFoodSort() {
        cacheManager.evictUrl(ApiUrls.Food.FOOD_USER_SORT_GET_URL)
        _uiState.update { it.copy(foodSortUiState = UiState.Loading) }

        repositoryScope.launch {
            fetchFoodSort().collect { state ->
                _uiState.update { it.copy(foodSortUiState = state) }
            }
        }
    }

    override fun loadFoodSort() {
        val uiState = _uiState.value.foodSortUiState
        if (uiState is UiState.Success || uiState is UiState.Error) return
        refreshFoodSort()
    }

    override fun updateFoodSort(
        request: FoodSortUpdateRequest
    ): Flow<UiState<String>> {
        return httpClient.makeRequest(
            apiCall = { apiService.updateFoodSort(request) },
            extractData = { it.foodSort },
            errMsg = "Failed to update food sort",
            invalidatedUrls = listOf(
                ApiUrls.Food.FOOD_USER_SORT_GET_URL,
                ApiUrls.Food.FOOD_USER_LIST_URL
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
        cacheManager.evictUrl(ApiUrls.Food.getLogsByDateUrl(date))

        repositoryScope.launch {
            fetchFoodLogs(date).collect { state ->
                _uiState.update { it.copy(foodLogsCache = it.foodLogsCache + (date to state)) }
            }
        }
    }

    override fun loadFoodLogs(date: String) {
        val cachedData = _uiState.value.foodLogsCache[date]
        if (cachedData is UiState.Success || cachedData is UiState.Error) {
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
                ApiUrls.Nutrient.getIntakesByDateUrl(date),
                ApiUrls.Food.getLogsByDateUrl(date)
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
                ApiUrls.Food.getLogsByDateUrl(date),
                ApiUrls.Nutrient.getIntakesByDateUrl(date)
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
                ApiUrls.Nutrient.getIntakesByDateUrl(date),
                ApiUrls.Food.getLogsByDateUrl(date)
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