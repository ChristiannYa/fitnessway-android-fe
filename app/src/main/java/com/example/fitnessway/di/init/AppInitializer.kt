package com.example.fitnessway.di.init

import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.fitnessway.data.repository.user.IUserRepository
import com.example.fitnessway.data.state.IAppStateStore
import com.example.fitnessway.di.modules.dateTimeFormatterModule
import com.example.fitnessway.di.modules.managerModules
import com.example.fitnessway.di.modules.networkPrivateModule
import com.example.fitnessway.di.modules.repositoryOperationsModule
import com.example.fitnessway.di.modules.repositoryPrivateModule
import com.example.fitnessway.di.modules.scopeModule
import com.example.fitnessway.di.modules.viewModelPrivateModule
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module
import org.koin.mp.KoinPlatform
import java.time.ZoneId

class AppInitializer(private val appStateStore: IAppStateStore) {

    private var authModules: List<Module> = emptyList()

    @OptIn(ExperimentalCoroutinesApi::class)
    fun initialize() {

        ProcessLifecycleOwner.get().lifecycleScope.launch {

            appStateStore.tokensStateHolder.state
                .filter { !it.isLoading }
                .flatMapLatest { tokensState ->
                    appStateStore.setIsAppReady(false)

                    when {
                        !tokensState.isAuthenticated -> {
                            appStateStore.setIsAppReady(true)
                            emptyFlow()
                        }

                        else -> {
                            if (authModules.isNotEmpty()) unloadKoinModules(authModules)

                            authModules = listOf(
                                scopeModule,
                                networkPrivateModule,
                                dateTimeFormatterModule,
                                managerModules,
                                repositoryPrivateModule,
                                viewModelPrivateModule,
                                repositoryOperationsModule

                            )
                            loadKoinModules(authModules)

                            val userRepo: IUserRepository = KoinPlatform.getKoin().get()
                            userRepo.load()
                            userRepo.uiState
                        }
                    }
                }
                .collect { userRepoUiState ->
                    when (val userUiState = userRepoUiState.userUiState) {

                        is UiState.Success -> userUiState.data.let { user ->
                            appStateStore.timezoneStateHolder.setTimezone(ZoneId.of(user.timezone))
                            appStateStore.setIsAppReady(true)
                        }

                        is UiState.Error -> appStateStore.clearStateHolders()

                        else -> {}
                    }
                }
        }
    }
}