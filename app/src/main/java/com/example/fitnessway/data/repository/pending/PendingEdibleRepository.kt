package com.example.fitnessway.data.repository.pending

import com.example.fitnessway.constants.Pagination
import com.example.fitnessway.data.model.m_26.EdibleAddRequest
import com.example.fitnessway.data.model.m_26.EdibleType
import com.example.fitnessway.data.model.m_26.PendingFood
import com.example.fitnessway.data.network.HttpClient
import com.example.fitnessway.data.network.ktor_client.PendingEdibleApiClient
import com.example.fitnessway.data.repository._state.RepositoryPagerState
import com.example.fitnessway.data.repository._state.loadMore
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.UiStatePager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class PendingEdibleRepository<T : RepositoryPagerState<PendingFood, T>>(
    edibleType: EdibleType,
    private val httpClient: HttpClient,
    private val apiClient: PendingEdibleApiClient,
    private val repositoryScope: CoroutineScope,
    initialState: T
) : IPendingEdibleRepository<T> {

    private val _uiState by lazy { MutableStateFlow(initialState) }
    override val uiState: StateFlow<T> = _uiState

    private val edibleTypeString = edibleType.name.lowercase()

    private fun fetch(offset: Long = 0L) =
        httpClient.makeRequest(
            apiCall = { apiClient.getList(Pagination.LIMIT, offset, edibleTypeString) },
            extractData = { it.pendingFoodsPagination },
            errMsg = "Failed to fetch $edibleTypeString requests",
            pathDescription = "$edibleTypeString request list"
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
        if (!_uiState.value.uiStatePager.uiState.hasState) refresh()
    }

    override fun loadMore() {
        _uiState.value.loadMore(_uiState, ::fetch, repositoryScope)
    }

    override suspend fun add(request: EdibleAddRequest): Flow<UiState<Unit>> =
        httpClient.makeRequest(
            apiCall = { apiClient.add(request) },
            extractData = { it.pendingFoodSubmitted },
            errMsg = "failed to submit $edibleTypeString request",
            pathDescription = "add $edibleTypeString request"
        )

    override suspend fun dismiss(id: Int): Flow<UiState<Unit>> =
        httpClient.makeRequest(
            apiCall = { apiClient.dismiss(id) },
            extractData = { Unit },
            errMsg = "Failed to dismiss $edibleTypeString review",
            pathDescription = "dismiss $edibleTypeString review"
        )

    override fun update(update: (T) -> T) {
        _uiState.update(update)
    }

    override fun clear() {
        _uiState.update { it.copyWithPager(UiStatePager()) }
    }
}