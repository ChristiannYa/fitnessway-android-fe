package com.example.fitnessway.data.repository.pending_food

import com.example.fitnessway.constants.Pagination
import com.example.fitnessway.data.model.m_26.PendingFood
import com.example.fitnessway.data.model.m_26.PendingFoodAddRequest
import com.example.fitnessway.data.network.HttpClient
import com.example.fitnessway.data.network.ktor_client.PendingFoodApiClient
import com.example.fitnessway.data.repository._state.loadMore
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.UiStatePager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PendingFoodRepositoryImpl(
    private val httpClient: HttpClient,
    private val apiClient: PendingFoodApiClient,
    private val repositoryScope: CoroutineScope,
) : IPendingFoodRepository {

    private val _uiState = MutableStateFlow(PendingFoodRepositoryUiState())
    override val uiState: StateFlow<PendingFoodRepositoryUiState> = _uiState

    private fun fetchPendingFoods(offset: Long = 0) =
        httpClient.makeRequest(
            apiCall = { apiClient.getPendingFoods(Pagination.LIMIT, offset) },
            extractData = { it.pendingFoodsPagination },
            errMsg = "Failed to fetch pending foods",
            pathDescription = "pending food list"
        )

    override fun refreshPendingFoods() {
        _uiState.update { it.copy(uiStatePager = UiStatePager()) }

        repositoryScope.launch {
            fetchPendingFoods().collect { state ->
                _uiState.update { it.copy(uiStatePager = UiStatePager(state)) }
            }
        }
    }

    override fun loadPendingFoods() {
        if (!_uiState.value.uiStatePager.uiState.hasState) refreshPendingFoods()
    }

    override fun loadMorePendingFoods() = _uiState.value.loadMore(_uiState, ::fetchPendingFoods, repositoryScope)

    override suspend fun addPendingFood(
        request: PendingFoodAddRequest,
    ): Flow<UiState<PendingFood>> =
        httpClient.makeRequest(
            apiCall = { apiClient.addPendingFood(request) },
            extractData = { it.pendingFoodSubmitted },
            errMsg = "Failed to add food request",
            pathDescription = "add pending food"
        ).onEach { state ->
            // @TODO: Remove refreshPendingFoods and move it to the view model
            if (state is UiState.Success) refreshPendingFoods()
        }

    override suspend fun dismissReview(id: Int): Flow<UiState<Unit>> =
        httpClient.makeRequest(
            apiCall = { apiClient.dismissReview(id) },
            extractData = { Unit },
            errMsg = "Failed to dismiss review",
            pathDescription = "dismiss reviewed pending food"
        )

    override fun updateState(update: (PendingFoodRepositoryUiState) -> PendingFoodRepositoryUiState) {
        _uiState.update(update)
    }

    override fun clearRepository() {
        _uiState.update { PendingFoodRepositoryUiState() }
    }
}