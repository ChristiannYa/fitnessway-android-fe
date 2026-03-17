package com.example.fitnessway.data.state.token

import kotlinx.coroutines.flow.StateFlow

interface ITokensStateHolder {
    val tokensState: StateFlow<TokensState>

    fun setAccessToken(token: String)
    fun setRefreshToken(token: String)
    fun clearTokens()
}