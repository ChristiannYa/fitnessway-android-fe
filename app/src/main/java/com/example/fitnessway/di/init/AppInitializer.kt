package com.example.fitnessway.di.init

import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.fitnessway.data.network.CacheManager
import com.example.fitnessway.data.repository.user.IUserRepository
import com.example.fitnessway.data.state.token.ITokensStateHolder
import com.example.fitnessway.data.state.user.IUserStateHolder
import com.example.fitnessway.util.Constants
import com.example.fitnessway.util.Formatters.logcat
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.launch

class AppInitializer(
    private val tokensStateHolder: ITokensStateHolder,
    private val userStateHolder: IUserStateHolder,
    private val userRepo: IUserRepository,
    private val cacheManager: CacheManager,
) {
    fun initialize() {
        ProcessLifecycleOwner.get().lifecycleScope.launch {
            tokensStateHolder.tokensState.collect { tokensState ->
                val userState = userStateHolder.userState.value

                // Fetch user if authenticated but user is not loaded
                if (tokensState.isAuthenticated &&
                    userState.user == null &&
                    !userState.isLoading
                ) {
                    userRepo.getUser().collect { uiState ->
                        when (uiState) {
                            is UiState.Idle -> {}
                            is UiState.Loading -> {}
                            is UiState.Success -> userStateHolder.setUser(uiState.data)
                            is UiState.Error -> {
                                logcat(
                                    message = "error collecting user state",
                                    level = Constants.LogLevel.ERROR
                                )
                                clearCachedData()
                            }
                        }
                    }
                }

                // Clear user data if not authenticated
                if (!tokensState.isAuthenticated && userState.user != null) clearCachedData
            }
        }
    }

    val clearCachedData = {
        tokensStateHolder.clearTokens()
        userStateHolder.clearUser()
        cacheManager.evictAll()
    }
}