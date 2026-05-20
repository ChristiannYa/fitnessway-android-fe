package com.example.fitnessway.data.repository.edible_recent_log

import com.example.fitnessway.constants.Pagination
import com.example.fitnessway.data.model.m_26.EdibleType
import com.example.fitnessway.data.model.m_26.FoodPreview
import com.example.fitnessway.data.model.m_26.PaginationParams
import com.example.fitnessway.data.network.HttpClient
import com.example.fitnessway.data.network.ktor_client.EdibleRecentLogApiClient
import com.example.fitnessway.data.repository._state.RepositoryPagerState
import com.example.fitnessway.data.repository._state.loadMore
import com.example.fitnessway.util.UiStatePager
import com.example.fitnessway.util.toEnum
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class EdibleRecentLogRepository<T : RepositoryPagerState<FoodPreview, T>>(
    edibleType: EdibleType,
    private val httpClient: HttpClient,
    private val apiClient: EdibleRecentLogApiClient,
    private val repositoryScope: CoroutineScope,
    initialState: T
) : IEdibleRecentLogRepository<T> {

    private val _uiState by lazy { MutableStateFlow(initialState) }
    override val uiState: StateFlow<T> = _uiState

    private val edibleTypeString = edibleType.name.lowercase()

    private fun fetch(offset: Long = 0L) =
        httpClient.makeRequest(
            apiCall = {
                apiClient.getLatestLoggedEdibles(
                    PaginationParams(Pagination.LIMIT, offset),
                    edibleType = edibleTypeString.toEnum()
                )
            },
            extractData = { it.recentlyLoggedEdiblesPagination },
            errMsg = "Failed to fetch recently logged ${edibleTypeString}s",
            pathDescription = "Recently logged ${edibleTypeString}s"
        )

    override fun refresh() {
        _uiState.update { it.copyWithPager(pager = UiStatePager()) }

        repositoryScope.launch {
            fetch().collect { state ->
                _uiState.update { it.copyWithPager(pager = UiStatePager(state)) }
            }
        }
    }

    override fun load() {
        val state = _uiState.value.uiStatePager.uiState
        if (!state.hasResult) refresh()
    }

    override fun loadMore() =
        _uiState.value.loadMore(_uiState, ::fetch, repositoryScope)

    override fun update(update: (T) -> T) =
        _uiState.update(update)

    override fun clear() =
        _uiState.update { it.copyWithPager(UiStatePager()) }
}