package com.example.fitnessway.data.state.token

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.example.fitnessway.data.constants.PreferencesStoreKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class TokenStateHolderImpl(
    private val dataStore: DataStore<Preferences>
) : ITokensStateHolder {
    private val _tokensState = MutableStateFlow(TokensState())
    override val tokensState: StateFlow<TokensState> = _tokensState

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        scope.launch {
            dataStore.data
                .catch { exception ->
                    if (exception is IOException) {
                        emit(emptyPreferences())
                    } else {
                        throw exception
                    }
                }
                .collect { prefs ->
                    _tokensState.value = TokensState(
                        accessToken = prefs[PreferencesStoreKeys.ACCESS_TOKEN],
                        refreshToken = prefs[PreferencesStoreKeys.REFRESH_TOKEN],
                        isLoading = false
                    )
                }
        }
    }

    override fun setAccessToken(token: String) {
        _tokensState.value = _tokensState.value.copy(accessToken = token)
        scope.launch { dataStore.edit { it[PreferencesStoreKeys.ACCESS_TOKEN] = token } }
    }

    override fun setRefreshToken(token: String) {
        _tokensState.value = _tokensState.value.copy(refreshToken = token)
        scope.launch { dataStore.edit { it[PreferencesStoreKeys.REFRESH_TOKEN] = token } }
    }

    override fun clearTokens() {
        _tokensState.value = TokensState(isLoading = false)
        scope.launch { dataStore.edit { it.clear() } }
    }
}