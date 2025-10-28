package com.example.fitnessway.data.state.token

import kotlinx.coroutines.flow.StateFlow

interface ITokensStateHolder {
    val tokensState: StateFlow<TokensState>

    fun setTokens(accessToken: String, refreshToken: String)

    fun clearTokens()
}