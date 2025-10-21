package com.example.fitnessway.data.state

import kotlinx.coroutines.flow.StateFlow

interface IAuthStateHolder {
   val authState: StateFlow<AuthState>

   fun register(
      name: String,
      email: String,
      password: String,
      confirmPassword: String,
      deviceName: String
   )

   fun login(
      email: String,
      password: String,
      deviceName: String
   )

   fun logout(
      refreshToken: String
   )

   fun refreshToken(
      refreshToken: String,
      deviceName: String
   )
}