package com.example.fitnessway.data.repository.pending_food

import com.example.fitnessway.constants.Pagination
import com.example.fitnessway.data.mappers.toPaginationOrNull
import com.example.fitnessway.data.model.m_26.PendingFood
import com.example.fitnessway.data.model.m_26.PendingFoodAddRequest
import com.example.fitnessway.data.network.HttpClient
import com.example.fitnessway.data.network.ktor_client.FoodApiClient
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
    private val apiClient: FoodApiClient,
    private val repositoryScope: CoroutineScope,
) : IPendingFoodRepository {

    private val _uiState = MutableStateFlow(PendingFoodRepositoryUiState())
    override val uiState: StateFlow<PendingFoodRepositoryUiState> = _uiState

    private fun fetchPendingFoods(offset: Long) =
        httpClient.makeRequest(
            apiCall = { apiClient.getPendingFoods(Pagination.LIMIT, offset) },
            extractData = { it.pendingFoodsPagination },
            errMsg = "Failed to fetch pending foods"
        )

    override fun refreshPendingFoods() {
        _uiState.update { it.copy(pendingFoodsUiStatePager = UiStatePager()) }

        repositoryScope.launch {
            fetchPendingFoods(offset = 0).collect { state ->
                _uiState.update { it.copy(pendingFoodsUiStatePager = UiStatePager(state)) }
            }
        }
    }

    override fun loadPendingFoods() {
        val uiState = _uiState.value.pendingFoodsUiStatePager.uiState
        if (uiState.hasFetched) return
        refreshPendingFoods()
    }

    override fun loadMorePendingFoods() {
        val pager = _uiState.value.pendingFoodsUiStatePager
        if (pager.isLoadingMore) return

        val pagination = pager.toPaginationOrNull() ?: return
        if (!pagination.hasMorePages) return

        _uiState.update {
            it.copy(pendingFoodsUiStatePager = pager.copy(isLoadingMore = true))
        }

        repositoryScope.launch {
            fetchPendingFoods(
                offset = pagination.currentPage * Pagination.LIMIT.toLong()
            ).collect { state ->
                when (state) {
                    is UiState.Success -> _uiState.update {
                        val current = it.pendingFoodsUiStatePager.toPaginationOrNull()
                        val accumulated = (current?.data ?: emptyList()) + state.data.data

                        it.copy(
                            pendingFoodsUiStatePager = UiStatePager(
                                uiState = UiState.Success(state.data.copy(data = accumulated)),
                                isLoadingMore = false
                            )
                        )
                    }

                    is UiState.Error -> _uiState.update {
                        it.copy(
                            pendingFoodsUiStatePager = it.pendingFoodsUiStatePager.copy(
                                isLoadingMore = false
                            )
                        )
                    }

                    else -> {}
                }
            }
        }
    }

    override suspend fun addPendingFood(
        request: PendingFoodAddRequest,
    ): Flow<UiState<PendingFood>> = httpClient.makeRequest(
        apiCall = { apiClient.addPendingFood(request) },
        extractData = { it.pendingFoodSubmitted },
        errMsg = "Failed to add food request"
    ).onEach { state ->
        if (state is UiState.Success) refreshPendingFoods()
    }

    override fun updateState(update: (PendingFoodRepositoryUiState) -> PendingFoodRepositoryUiState) {
        _uiState.update(update)
    }

    override fun clearRepository() {
        _uiState.update { PendingFoodRepositoryUiState() }
    }
}