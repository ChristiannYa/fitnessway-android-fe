package com.example.fitnessway.data.repository.user_food

import com.example.fitnessway.data.model.MFood.Api.Req.FoodAddRequest
import com.example.fitnessway.data.model.MFood.Api.Req.FoodUpdateRequest
import com.example.fitnessway.data.model.MFood.Model.FoodInformation
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
            errMsg = "Failed to get foods",
            pathDescription = "food list"
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
    ): Flow<UiState<FoodInformation>> =
        httpClient.makeRequest(
            apiCall = { apiClient.addFood(request) },
            extractData = { it.foodCreated },
            errMsg = "Failed to add food",
            pathDescription = "add food"
        ).onEach { state ->
            // @TODO: Should be done in view model
            if (state is UiState.Success) {
                refreshFoods()
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
                pathDescription = "update food"
            ).collect { state ->
                _updateFoodFlow.emit(state)
            }
        }

        return _updateFoodFlow
    }

    override suspend fun deleteFood(
        foodId: Int
    ): Flow<UiState<FoodInformation>> =
        httpClient.makeRequest(
            apiCall = { apiClient.deleteFood(foodId) },
            extractData = { it.foodDeleted },
            errMsg = "Failed to delete food",
            pathDescription = "delete food"
        )

    override fun updateState(update: (FoodRepositoryUiState) -> FoodRepositoryUiState) = _uiState.update(update)

    override fun clearRepository() = _uiState.update { FoodRepositoryUiState() }
}