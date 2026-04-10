package com.example.fitnessway.data.repository.food_log

import com.example.fitnessway.constants.Pagination
import com.example.fitnessway.data.mappers.toPaginationOrNull
import com.example.fitnessway.data.model.MFood.Model.FoodLogData
import com.example.fitnessway.data.model.m_26.FoodLog
import com.example.fitnessway.data.model.m_26.FoodLogAddRequest
import com.example.fitnessway.data.model.m_26.FoodLogUpdateRequest
import com.example.fitnessway.data.model.m_26.FoodLogsCategorized
import com.example.fitnessway.data.model.m_26.PaginationParams
import com.example.fitnessway.data.network.ApiUrls
import com.example.fitnessway.data.network.HttpClient
import com.example.fitnessway.data.network.ktor_client.FoodLogApiClient
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.UiStatePager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FoodLogRepositoryImpl(
    private val httpClient: HttpClient,
    private val apiClient: FoodLogApiClient,
    private val repositoryScope: CoroutineScope
) : IFoodLogRepository {

    private val _uiState = MutableStateFlow(FoodLogRepositoryUiState())
    override val uiState: StateFlow<FoodLogRepositoryUiState> = _uiState

    private fun fetchFoodLogs(date: String): Flow<UiState<FoodLogsCategorized>> =
        httpClient.makeRequest(
            apiCall = { apiClient.getByDate(date) },
            extractData = { it.foodLogs },
            errMsg = "Failed to get logs"
        )

    override fun refreshFoodLogs(date: String) {
        repositoryScope.launch {
            fetchFoodLogs(date).collect { state ->
                _uiState.update { it.copy(foodLogs = it.foodLogs + (date to state)) }
            }
        }
    }

    override fun loadFoodLogs(date: String) {
        val uiState = _uiState.value.foodLogs[date]
        uiState?.let { if (it.hasState) return }
        refreshFoodLogs(date)
    }

    override suspend fun addFoodLog(
        request: FoodLogAddRequest,
        date: String
    ): Flow<UiState<FoodLog>> = httpClient.makeRequest(
        apiCall = { apiClient.add(request) },
        extractData = { it.foodLogAdded },
        errMsg = "Failed to add log"
    )

    override suspend fun updateFoodLog(
        request: FoodLogUpdateRequest,
        date: String
    ): Flow<UiState<FoodLog>> = httpClient.makeRequest(
        apiCall = { apiClient.update(request) },
        extractData = { it.foodLogUpdated },
        errMsg = "Failed to update log",
        invalidatedUrls = listOf(
            ApiUrls.FoodLog.getListByDateUrl(date),
            ApiUrls.Nutrient.getIntakesByDateUrl(date)
        )
    )

    override suspend fun deleteFoodLog(
        foodLogId: Int,
        date: String
    ): Flow<UiState<FoodLogData>> = httpClient.makeRequest(
        apiCall = { apiClient.delete(foodLogId) },
        extractData = { it.foodLogDeleted },
        errMsg = "Failed to delete log"
    )

    override fun clearFoodLogsUiCache() = _uiState.update { it.copy(foodLogs = emptyMap()) }

    private fun fetchRecentlyLogged(offset: Long) = httpClient.makeRequest(
        apiCall = {
            apiClient.getLatestLoggedFoods(
                PaginationParams(Pagination.LIMIT, offset)
            )
        },
        extractData = { it.recentlyLoggedFoodsPagination },
        errMsg = "Failed to fetch recently logged foods"
    )

    override fun refreshRecentlyLogged() {
        _uiState.update {
            it.copy(
                recentlyLogged = UiStatePager()
            )
        }

        repositoryScope.launch {
            fetchRecentlyLogged(offset = 0).collect { state ->
                _uiState.update {
                    it.copy(
                        recentlyLogged = UiStatePager(state)
                    )
                }
            }
        }
    }

    override fun loadRecentlyLogged() {
        val uiState = _uiState.value.recentlyLogged.uiState
        if (uiState.hasState) return
        refreshRecentlyLogged()
    }

    override fun loadMoreRecentlyLogged() {
        val pager = _uiState.value.recentlyLogged
        if (pager.isLoadingMore) return

        val pagination = pager.toPaginationOrNull() ?: return
        if (!pagination.hasMorePages) return

        _uiState.update {
            it.copy(
                recentlyLogged = pager.copy(
                    isLoadingMore = true
                )
            )
        }

        repositoryScope.launch {
            fetchRecentlyLogged(
                offset = pagination.currentPage * Pagination.LIMIT.toLong()
            ).collect { state ->
                when (state) {
                    is UiState.Success -> _uiState.update {
                        val current = it.recentlyLogged.toPaginationOrNull()
                        val accumulated = (current?.data ?: emptyList()) + state.data.data

                        it.copy(
                            recentlyLogged = UiStatePager(
                                uiState = UiState.Success(
                                    data = state.data.copy(
                                        data = accumulated
                                    )
                                )
                            )
                        )
                    }

                    is UiState.Error -> _uiState.update {
                        it.copy(
                            recentlyLogged = it.recentlyLogged.copy(
                                isLoadingMore = false
                            )
                        )
                    }

                    else -> {}
                }
            }
        }
    }


    override fun updateState(update: (FoodLogRepositoryUiState) -> FoodLogRepositoryUiState) = _uiState.update(update)

    override fun clearRepository() = _uiState.update { FoodLogRepositoryUiState() }
}
