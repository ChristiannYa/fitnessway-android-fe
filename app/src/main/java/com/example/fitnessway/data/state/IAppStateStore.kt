package com.example.fitnessway.data.state

import com.example.fitnessway.data.state.timezone.ITimezoneStateHolder
import com.example.fitnessway.data.state.token.ITokensStateHolder
import kotlinx.coroutines.flow.StateFlow

interface IAppStateStore {
    val tokensStateHolder: ITokensStateHolder
    val timezoneStateHolder: ITimezoneStateHolder

    val isAppReady: StateFlow<Boolean>

    fun setAppReady()
}