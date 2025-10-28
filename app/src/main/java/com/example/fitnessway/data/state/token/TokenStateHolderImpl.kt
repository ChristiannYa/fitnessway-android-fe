package com.example.fitnessway.data.state.token

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

class TokenStateHolderImpl(
    private val dataStore: DataStore<AuthTokens>
) : ITokensStateHolder {
    private val _tokensState = MutableStateFlow(TokensState())
    override val tokensState: StateFlow<TokensState> = _tokensState

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
                    _tokensState.value = TokensState(
                        accessToken = tokens.accessToken.takeIf { it.isNotEmpty() },
                        refreshToken = tokens.refreshToken.takeIf { it.isNotEmpty() },
                        isLoading = false
                    )
                }
        }
    }

    override fun setTokens(accessToken: String, refreshToken: String) {
        // Update in-memory state immediately
        _tokensState.value = TokensState(
            accessToken,
            refreshToken,
            isLoading = false
        )

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

    override fun clearTokens() {
        // Clear in-memory state immediately
        _tokensState.value = TokensState(isLoading = false)

        // Clear DataStore
        scope.launch {
            dataStore.updateData {
                AuthTokens.getDefaultInstance()
            }
        }
    }
}