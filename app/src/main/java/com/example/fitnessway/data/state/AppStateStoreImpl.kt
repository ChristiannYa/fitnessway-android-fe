package com.example.fitnessway.data.state

import com.example.fitnessway.data.state.token.ITokensStateHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AppStateStoreImpl(
    override val tokensStateHolder: ITokensStateHolder,
) : IAppStateStore {

    private val _isAppReady = MutableStateFlow(false)
    override val isAppReady: StateFlow<Boolean> = _isAppReady

    override fun setAppReady() {
        _isAppReady.value = true
    }
}