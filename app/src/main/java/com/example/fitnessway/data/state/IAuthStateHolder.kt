package com.example.fitnessway.data.state

import kotlinx.coroutines.flow.StateFlow

interface IAuthStateHolder {
   val authState: StateFlow<AuthState>

   fun setAuth(
      accessToken: String,
      refreshToken: String
   )

   fun clearAuth()
}