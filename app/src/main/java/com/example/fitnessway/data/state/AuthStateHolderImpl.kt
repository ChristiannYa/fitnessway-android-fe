package com.example.fitnessway.data.state

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class AuthStateHolderImpl : IAuthStateHolder {
   private val _authState = MutableStateFlow(AuthState())
   override val authState: StateFlow<AuthState> = _authState

   override fun setAuth(accessToken: String, refreshToken: String) {
      _authState.update {
         AuthState(
            accessToken = accessToken,
            refreshToken = refreshToken
         )
      }
   }

   override fun clearAuth() {
      _authState.update { AuthState() }
   }
}