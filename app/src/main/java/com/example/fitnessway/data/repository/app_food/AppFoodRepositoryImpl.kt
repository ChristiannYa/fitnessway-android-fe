package com.example.fitnessway.data.repository.app_food

import com.example.fitnessway.constants.Pagination
import com.example.fitnessway.data.mappers.toPaginationOrNull
import com.example.fitnessway.data.model.m_26.PaginationParams
import com.example.fitnessway.data.network.HttpClient
import com.example.fitnessway.data.network.ktor_client.AppFoodApiClient
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.UiStatePager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppFoodRepositoryImpl(
    private val httpClient: HttpClient,
    private val apiClient: AppFoodApiClient,
    private val repositoryScope: CoroutineScope
) : IAppFoodRepository {
    private val _uiState = MutableStateFlow(AppFoodRepositoryUiState())
    override val uiState: StateFlow<AppFoodRepositoryUiState> = _uiState

    private fun fetchAppFoods(query: String, offset: Long) =
        httpClient.makeRequest(
            apiCall = {
                apiClient.searchAppFoods(
                    query = query,
                    params = PaginationParams(Pagination.LIMIT, offset)
                )
            },
            extractData = { it.appFoodsPagination },
            errMsg = "Failed to fetch app foods"
        )

    private var searchJob: Job? = null

    override fun searchAppFoods(query: String) {
        searchJob?.cancel()

        _uiState.update {
            it.copy(
                appFoodsUiStatePager = UiStatePager(),
            )
        }

        searchJob = repositoryScope.launch {
            fetchAppFoods(query, 0).collect { state ->
                _uiState.update {
                    it.copy(
                        appFoodsUiStatePager = UiStatePager(state)
                    )
                }
            }
        }
    }

    override fun loadMoreAppFoods(query: String) {
        val pager = _uiState.value.appFoodsUiStatePager
        if (pager.isLoadingMore) return

        val pagination = pager.toPaginationOrNull() ?: return
        if (!pagination.hasMorePages) return

        _uiState.update {
            it.copy(
                appFoodsUiStatePager = pager.copy(isLoadingMore = true)
            )
        }

        repositoryScope.launch {
            fetchAppFoods(
                query = query,
                offset = pagination.currentPage * Pagination.LIMIT.toLong()
            ).collect { state ->
                when (state) {
                    is UiState.Success -> _uiState.update {
                        val current = it.appFoodsUiStatePager.toPaginationOrNull()
                        val accumulated = (current?.data ?: emptyList()) + state.data.data

                        it.copy(
                            appFoodsUiStatePager = UiStatePager(
                                uiState = UiState.Success(
                                    data = state.data.copy(data = accumulated),
                                ),
                                isLoadingMore = false
                            )
                        )
                    }

                    is UiState.Error -> _uiState.update {
                        it.copy(
                            appFoodsUiStatePager = it.appFoodsUiStatePager.copy(
                                isLoadingMore = false
                            )
                        )
                    }

                    else -> {}
                }
            }
        }
    }

    override fun clearAppFoods() {
        searchJob?.cancel()
        _uiState.update { it.copy(appFoodsUiStatePager = UiStatePager(UiState.Idle)) }
    }
}