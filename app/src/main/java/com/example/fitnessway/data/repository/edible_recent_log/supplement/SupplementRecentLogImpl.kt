package com.example.fitnessway.data.repository.edible_recent_log.supplement

import com.example.fitnessway.constants.Pagination
import com.example.fitnessway.data.model.m_26.EdibleType
import com.example.fitnessway.data.model.m_26.PaginationParams
import com.example.fitnessway.data.network.HttpClient
import com.example.fitnessway.data.network.ktor_client.EdibleRecentLogApiClient
import com.example.fitnessway.data.repository._state.loadMore
import com.example.fitnessway.util.UiStatePager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SupplementRecentLogImpl(
    private val httpClient: HttpClient,
    private val apiClient: EdibleRecentLogApiClient,
    private val repositoryScope: CoroutineScope
) : ISupplementRecentLog {

    private val _uiState = MutableStateFlow(SupplementRecentLogUiState())
    override val uiState: StateFlow<SupplementRecentLogUiState> = _uiState

    private fun fetch(offset: Long = 0) =
        httpClient.makeRequest(
            apiCall = {
                apiClient.getLatestLoggedEdibles(
                    PaginationParams(Pagination.LIMIT, offset),
                    EdibleType.SUPPLEMENT
                )
            },
            extractData = { it.recentlyLoggedEdiblesPagination },
            errMsg = "Failed to fetch recently logged supplements",
            pathDescription = "recently logged supplement list"
        )


    override fun refresh() {
        _uiState.update { it.copy(uiStatePager = UiStatePager()) }

        repositoryScope.launch {
            fetch().collect { state ->
                _uiState.update {
                    it.copy(uiStatePager = UiStatePager(state))
                }
            }
        }
    }

    override fun load() {
        if (!_uiState.value.uiStatePager.uiState.hasState) refresh()

    }

    override fun loadMore() = _uiState.value.loadMore(_uiState, ::fetch, repositoryScope)

    override fun updateState(update: (SupplementRecentLogUiState) -> SupplementRecentLogUiState) =
        _uiState.update(update)

    override fun clear() = _uiState.update { SupplementRecentLogUiState() }
}