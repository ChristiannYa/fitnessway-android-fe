package com.example.fitnessway.data.state

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import com.fitnessway.data.AuthTokens
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class AuthStateHolderImpl(
    private val dataStore: DataStore<AuthTokens>
) : IAuthStateHolder {
    private val _authState = MutableStateFlow(AuthState())
    override val authState: StateFlow<AuthState> = _authState

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        // Load tokens from DataStore on init
        scope.launch {
            dataStore.data
                .catch { exception ->
                    if (exception is IOException) {
                        emit(AuthTokens.getDefaultInstance())
                    } else {
                        throw exception
                    }
                }
                .collect { tokens ->
                    _authState.value = AuthState(
                        accessToken = tokens.accessToken.takeIf { it.isNotEmpty() },
                        refreshToken = tokens.refreshToken.takeIf { it.isNotEmpty() },
                        isLoading = false
                    )
                }
        }
    }

    override fun setAuth(accessToken: String, refreshToken: String) {
        // Update in-memory state immediately
        _authState.value = AuthState(accessToken, refreshToken, false)

        // Persist it to DataStore
        scope.launch {
            dataStore.updateData { currentTokens ->
                currentTokens.toBuilder()
                    .setAccessToken(accessToken)
                    .setRefreshToken(refreshToken)
                    .build()
            }
        }
    }

    override fun clearAuth() {
        // Clear in-memory state immediately
        _authState.value = AuthState(isLoading = false)

        // Clear DataStore
        scope.launch {
            dataStore.updateData {
                AuthTokens.getDefaultInstance()
            }
        }
    }
}