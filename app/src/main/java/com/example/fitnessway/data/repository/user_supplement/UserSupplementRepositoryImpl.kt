package com.example.fitnessway.data.repository.user_supplement

import com.example.fitnessway.constants.Pagination
import com.example.fitnessway.data.network.HttpClient
import com.example.fitnessway.data.network.ktor_client.UserSupplementApiClient
import com.example.fitnessway.data.repository._state.loadMore
import com.example.fitnessway.util.UiStatePager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UserSupplementRepositoryImpl(
    private val httpClient: HttpClient,
    private val apiClient: UserSupplementApiClient,
    private val repositoryScope: CoroutineScope
) : IUserSupplementRepository {

    private val _uiState = MutableStateFlow(UserSupplementRepositoryUiState())
    override val uiState: StateFlow<UserSupplementRepositoryUiState> = _uiState

    private fun fetch(offset: Long = 0) =
        httpClient.makeRequest(
            apiCall = {
                apiClient.get(
                    limit = Pagination.LIMIT,
                    offset = offset
                )
            },
            extractData = { it.userEdiblesPagination },
            errMsg = "Failed to get your supplements",
            pathDescription = "user supplement list"
        )

    override fun refresh() {
        _uiState.update { it.copy(uiStatePager = UiStatePager()) }

        repositoryScope.launch {
            fetch().collect { state ->
                _uiState.update { it.copy(uiStatePager = UiStatePager(state)) }
            }
        }
    }

    override fun load() {
        if (!_uiState.value.uiStatePager.uiState.hasState) refresh()
    }

    override fun loadMore() = _uiState.value.loadMore(_uiState, ::fetch, repositoryScope)

    override fun update(
        update: (UserSupplementRepositoryUiState) -> UserSupplementRepositoryUiState
    ) = _uiState.update(update)

    override fun clear() = _uiState.update { UserSupplementRepositoryUiState() }

}