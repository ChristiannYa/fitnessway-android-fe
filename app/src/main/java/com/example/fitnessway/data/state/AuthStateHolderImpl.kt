package com.example.fitnessway.data.state

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class AuthStateHolderImpl : IAuthStateHolder {
   private val _authState = MutableStateFlow(AuthState())
   override val authState: StateFlow<AuthState> = _authState

   override fun register(
      name: String,
      email: String,
      password: String,
      confirmPassword: String,
      deviceName: String
   ) {
      _authState.update { current ->
         current.copy()
      }
   }

   override fun login(
      email: String,
      password: String,
      deviceName: String
   ) {
      _authState.update { current ->
         current.copy()
      }
   }

   override fun logout(
      refreshToken: String
   ) {
      _authState.update { current ->
         current.copy()
      }
   }

   override fun refreshToken(
      refreshToken: String,
      deviceName: String
   ) {
      _authState.update { current ->
         current.copy()
      }
   }
}