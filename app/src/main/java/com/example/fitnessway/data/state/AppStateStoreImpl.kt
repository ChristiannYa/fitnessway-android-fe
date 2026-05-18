package com.example.fitnessway.data.state

import com.example.fitnessway.data.state.timezone.ITimezoneStateHolder
import com.example.fitnessway.data.state.token.ITokensStateHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AppStateStoreImpl(
    override val tokensStateHolder: ITokensStateHolder,
    override val timezoneStateHolder: ITimezoneStateHolder
) : IAppStateStore {

    private val _isAppReady = MutableStateFlow(false)
    override val isAppReady: StateFlow<Boolean> = _isAppReady

    override fun setIsAppReady(isAppReady: Boolean) {
        _isAppReady.value = isAppReady
    }

    override fun clearStateHolders() {
        tokensStateHolder.clearTokens()
    }
}