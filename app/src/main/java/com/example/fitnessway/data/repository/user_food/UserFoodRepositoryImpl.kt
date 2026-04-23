package com.example.fitnessway.data.repository.user_food

import com.example.fitnessway.constants.Pagination
import com.example.fitnessway.data.mappers.toPaginationOrNull
import com.example.fitnessway.data.model.MFood.Api.Req.FoodAddRequest
import com.example.fitnessway.data.model.MFood.Api.Req.FoodUpdateRequest
import com.example.fitnessway.data.model.MFood.Model.FoodInformation
import com.example.fitnessway.data.model.m_26.PaginationResult
import com.example.fitnessway.data.model.m_26.UserEdible
import com.example.fitnessway.data.network.HttpClient
import com.example.fitnessway.data.network.ktor_client.UserFoodApiClient
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.UiStatePager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UserFoodRepositoryImpl(
    private val httpClient: HttpClient,
    private val apiClient: UserFoodApiClient,
    private val repositoryScope: CoroutineScope,
) : IUserFoodRepository {

    private val _uiState = MutableStateFlow(UserFoodRepositoryUiState())
    override val uiState: StateFlow<UserFoodRepositoryUiState> = _uiState

    private fun fetchFoods(offset: Long): Flow<UiState<PaginationResult<UserEdible>>> =
        httpClient.makeRequest(
            apiCall = {
                apiClient.getFoods(
                    limit = Pagination.LIMIT,
                    offset = offset
                )
            },
            extractData = { it.userEdiblesPagination },
            errMsg = "Failed to get your foods",
            pathDescription = "user food list"
        )

    override fun refreshFoods() {
        _uiState.update { it.copy(foodsUiStatePager = UiStatePager()) }

        repositoryScope.launch {
            fetchFoods(offset = 0).collect { state ->
                _uiState.update { it.copy(foodsUiStatePager = UiStatePager(state)) }
            }
        }
    }

    override fun loadMoreFoods() {
        val pager = _uiState.value.foodsUiStatePager
        if (pager.isLoadingMore) return

        val pagination = pager.toPaginationOrNull() ?: return
        if (!pagination.hasMorePages) return

        _uiState.update {
            it.copy(foodsUiStatePager = pager.copy(isLoadingMore = true))
        }

        repositoryScope.launch {
            fetchFoods(pagination.getNextOffset()).collect { state ->
                when (state) {
                    is UiState.Success -> _uiState.update {
                        val current = it.foodsUiStatePager.toPaginationOrNull()
                        val accumulated = (current?.data ?: emptyList()) + state.data.data

                        it.copy(
                            foodsUiStatePager = UiStatePager(
                                uiState = UiState.Success(state.data.copy(data = accumulated)),
                                isLoadingMore = false
                            )
                        )
                    }

                    is UiState.Error -> _uiState.update {
                        it.copy(
                            foodsUiStatePager = it.foodsUiStatePager.copy(
                                isLoadingMore = false
                            )
                        )
                    }

                    else -> {}
                }
            }
        }
    }

    override fun loadFoods() {
        val uiState = _uiState.value.foodsUiStatePager.uiState
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

    override fun updateState(update: (UserFoodRepositoryUiState) -> UserFoodRepositoryUiState) = _uiState.update(update)

    override fun clearRepository() = _uiState.update { UserFoodRepositoryUiState() }
}