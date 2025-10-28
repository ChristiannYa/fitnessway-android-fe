package com.example.fitnessway.data.state.auth

data class AuthState (
   val accessToken: String? = null,
   val refreshToken: String? = null,
   val isLoading: Boolean = true,
) {
   val isAuthenticated: Boolean
      get() = accessToken != null && refreshToken != null
}