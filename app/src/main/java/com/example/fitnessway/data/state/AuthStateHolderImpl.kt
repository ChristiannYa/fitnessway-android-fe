package com.example.fitnessway.data.state

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

// @TODO: Implement `SharedPreferences`, DataStore`, or encrypted storage

class AuthStateHolderImpl : IAuthStateHolder {
   private val _authState = MutableStateFlow(AuthState())
   override val authState: StateFlow<AuthState> = _authState

   override fun setAuth(accessToken: String, refreshToken: String) {
      _authState.update { AuthState(accessToken, refreshToken) }
   }

   // Currently since no storage has been implemented, this just creates a new
   // instance of `AuthState` with default values (the tokens are null by default)
   override fun clearAuth() {
      _authState.update { AuthState() }
   }
}