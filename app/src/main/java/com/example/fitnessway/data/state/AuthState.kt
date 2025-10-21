package com.example.fitnessway.data.state

data class AuthState (
   val isAuthenticated: Boolean = false,
   val userId: String? = null,
   val accessToken: String? = null,
   val refreshToken: String? = null,
)