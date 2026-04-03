package com.example.fitnessway.di.init

import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.fitnessway.data.repository.user.IUserRepository
import com.example.fitnessway.data.state.token.ITokensStateHolder
import com.example.fitnessway.data.state.user.IUserStateHolder
import com.example.fitnessway.di.modules.loadAppDateTimeFormatterModule
import com.example.fitnessway.di.modules.loadManagerModules
import com.example.fitnessway.util.Constants
import com.example.fitnessway.util.Formatters.logcat
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.launch
import org.koin.core.context.loadKoinModules
import java.time.ZoneId

class AppInitializer(
    private val tokensStateHolder: ITokensStateHolder,
    private val userStateHolder: IUserStateHolder,
    private val userRepo: IUserRepository,
) {
    fun initialize() {
        ProcessLifecycleOwner.get().lifecycleScope.launch {
            tokensStateHolder.tokensState.collect { tokensState ->
                val userState = userStateHolder.userState.value

                if (tokensState.isAuthenticated &&
                    userState.user == null &&
                    !userState.isLoading
                ) {
                    userRepo.getUser().collect { uiState ->
                        when (uiState) {
                            is UiState.Success -> {
                                val user = uiState.data
                                userStateHolder.setUser(user)

                                val timezoneId = ZoneId.of(user.timezone)

                                loadKoinModules(
                                    listOf(
                                        loadAppDateTimeFormatterModule(timezoneId),
                                        loadManagerModules(timezoneId)
                                    )
                                )
                            }

                            is UiState.Error -> {
                                logcat(
                                    message = "error collecting user state",
                                    level = Constants.LogLevel.ERROR
                                )

                                clearCachedData()
                            }

                            else -> {}
                        }
                    }
                }

                if (!tokensState.isAuthenticated && userState.user != null) clearCachedData()
            }
        }
    }

    fun clearCachedData() {
        tokensStateHolder.clearTokens()
        userStateHolder.clearUser()
    }
}