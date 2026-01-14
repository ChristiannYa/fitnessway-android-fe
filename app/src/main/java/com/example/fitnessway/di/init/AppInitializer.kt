package com.example.fitnessway.di.init

import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
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
    private val userRepo: IUserRepository
) {
    fun initialize() {
        ProcessLifecycleOwner.get().lifecycleScope.launch {
            tokensStateHolder.tokensState.collect { tokensState ->
                logcat(message = tokensState.accessToken.toString())

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
                                    message = "error collecting token states",
                                    level = Constants.LogLevel.ERROR
                                )
                                tokensStateHolder.clearTokens()
                                userStateHolder.clearUser()
                            }
                        }
                    }
                }

                // Clear user if not authenticated
                if (!tokensState.isAuthenticated && userState.user != null) {
                    userStateHolder.clearUser()
                }
            }
        }
    }
}