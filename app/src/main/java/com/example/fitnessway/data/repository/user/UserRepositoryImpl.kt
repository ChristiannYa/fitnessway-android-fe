package com.example.fitnessway.data.repository.user

import com.example.fitnessway.data.model.MUser.Model.User
import com.example.fitnessway.data.model.m_26.UserTimezoneSetRequest
import com.example.fitnessway.data.network.HttpClient
import com.example.fitnessway.data.network.ktor_client.UserApiClient
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UserRepositoryImpl(
    private val httpClient: HttpClient,
    private val apiClient: UserApiClient,
    private val repositoryScope: CoroutineScope
) : IUserRepository {

    private val _uiState = MutableStateFlow(UserRepositoryUiState())
    override val uiState: StateFlow<UserRepositoryUiState> = _uiState

    private fun fetchUser(): Flow<UiState<User>> =
        httpClient.makeRequest(
            apiCall = apiClient::getUser,
            extractData = { it.user },
            errMsg = "Failed to get user data",
            pathDescription = "user"
        )

    override fun refresh() {
        repositoryScope.launch {
            fetchUser().collect { state ->
                _uiState.update { it.copy(userUiState = state) }
            }
        }
    }

    override fun load() {
        val uiState = _uiState.value.userUiState
        if (uiState.hasResult) return
        refresh()
    }

    override suspend fun setTimezone(request: UserTimezoneSetRequest): Flow<UiState<Unit>> =
        httpClient.makeRequest(
            apiCall = { apiClient.setTimezone(request) },
            extractData = { Unit },
            errMsg = "Failed to set timezone",
            pathDescription = "user timezone set"
        )

    override fun update(update: (UserRepositoryUiState) -> UserRepositoryUiState) =
        _uiState.update(update)

    override fun clear() =
        _uiState.update { UserRepositoryUiState() }
}
