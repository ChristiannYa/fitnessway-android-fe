package com.example.fitnessway.data.state.token

data class TokensState (
   val accessToken: String? = null,
   val refreshToken: String? = null,
   val isLoading: Boolean = true,
) {
   val isAuthenticated: Boolean
      get() = accessToken != null && refreshToken != null
}