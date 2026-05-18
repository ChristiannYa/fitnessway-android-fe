package com.example.fitnessway.data.state.token

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.example.fitnessway.constants.PreferencesStoreKeys
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
    private val _state = MutableStateFlow(TokensState())
    override val state: StateFlow<TokensState> = _state

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
                    val accessToken = prefs[PreferencesStoreKeys.ACCESS_TOKEN]
                    val refreshToken = prefs[PreferencesStoreKeys.REFRESH_TOKEN]

                    _state.value = TokensState(
                        accessToken = accessToken,
                        refreshToken = refreshToken,
                        isLoading = false
                    )
                }
        }
    }

    override fun setTokens(accessToken: String, refreshToken: String) {
        scope.launch {
            dataStore.edit {
                it[PreferencesStoreKeys.ACCESS_TOKEN] = accessToken
                it[PreferencesStoreKeys.REFRESH_TOKEN] = refreshToken
            }
        }
    }

    override fun setAccessToken(token: String) {
        scope.launch {
            dataStore.edit {
                it[PreferencesStoreKeys.ACCESS_TOKEN] = token
            }
        }
    }

    override fun setRefreshToken(token: String) {
        scope.launch {
            dataStore.edit {
                it[PreferencesStoreKeys.REFRESH_TOKEN] = token
            }
        }
    }

    override fun clearTokens() {
        scope.launch { dataStore.edit { it.clear() } }
    }
}