package com.example.fitnessway.data.state

import com.example.fitnessway.data.state.token.ITokensStateHolder
import kotlinx.coroutines.flow.StateFlow

interface IAppStateStore {
    val tokensStateHolder: ITokensStateHolder

    val isAppReady: StateFlow<Boolean>

    fun setAppReady()
}