package com.example.fitnessway.data.repository

import com.example.fitnessway.util.UiState
import kotlinx.coroutines.flow.Flow

interface IAuthRepository {
   suspend fun login(
      email: String,
      password: String,
      deviceName: String
   ): Flow<UiState<Unit>>

   /*
   suspend fun register(
      name: String,
      email: String,
      password: String,
      confirmPassword: String,
      deviceName: String
   ): Flow<UiState<Unit>>

   suspend fun logout(
      refreshToken: String
   ): Flow<UiState<Unit>>

   suspend fun refreshToken(
      refreshToken: String,
      deviceName: String
   ): Flow<UiState<Unit>>
    */
}