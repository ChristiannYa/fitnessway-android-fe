package com.example.fitnessway.di.init

import android.util.Log
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.fitnessway.data.repository.user.IUserRepository
import com.example.fitnessway.data.state.token.ITokensStateHolder
import com.example.fitnessway.data.state.user.IUserStateHolder
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.launch

class AppInitializer(
    private val tokensStetHolder: ITokensStateHolder,
    private val userStateHolder: IUserStateHolder,
    private val userRepo: IUserRepository
) {
    fun initialize() {
        ProcessLifecycleOwner.get().lifecycleScope.launch {
            tokensStetHolder.tokensState.collect { tokensState ->
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
                                Log.d(
                                    "fitnessway",
                                    "AppInitializer got an error when collecting user ui state"
                                )
                                Log.d("fitnessway", "clearing tokens")
                                tokensStetHolder.clearTokens()
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