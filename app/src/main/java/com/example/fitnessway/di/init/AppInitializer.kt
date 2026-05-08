package com.example.fitnessway.di.init

import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.fitnessway.data.repository.user.IUserRepository
import com.example.fitnessway.data.state.IAppStateStore
import com.example.fitnessway.di.modules.loadAppDateTimeFormatterModule
import com.example.fitnessway.di.modules.loadManagerModules
import com.example.fitnessway.di.modules.privateViewModelModule
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import org.koin.core.context.loadKoinModules
import java.time.ZoneId

class AppInitializer(
    private val appStateStore: IAppStateStore,
    private val userRepo: IUserRepository,
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    fun initialize() {
        ProcessLifecycleOwner.get().lifecycleScope.launch {
            appStateStore.tokensStateHolder.state
                .flatMapLatest { tokensState ->
                    if (!tokensState.isAuthenticated) {
                        clearStateHolders()
                        appStateStore.setAppReady()
                        return@flatMapLatest emptyFlow()
                    }

                    userRepo.load()
                    userRepo.uiState
                }
                .collect { userRepoUiState ->
                    when (val userUiState = userRepoUiState.userUiState) {

                        is UiState.Success -> userUiState.data.let { user ->
                            val timezoneId = ZoneId.of(user.timezone)
                            appStateStore.timezoneStateHolder.setTimezone(timezoneId)

                            loadKoinModules(
                                listOf(
                                    loadAppDateTimeFormatterModule(),
                                    loadManagerModules(),
                                    privateViewModelModule
                                )
                            )

                            appStateStore.setAppReady()
                        }

                        is UiState.Error -> clearStateHolders()

                        else -> {}
                    }
                }
        }
    }

    fun clearStateHolders() {
        appStateStore.tokensStateHolder.clearTokens()
    }
}