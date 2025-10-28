package com.example.fitnessway.data.repository.auth

import com.example.fitnessway.util.UiState
import kotlinx.coroutines.flow.Flow

interface IAuthRepository {
   suspend fun login(
      email: String,
      password: String,
      deviceName: String
   ): Flow<UiState<Unit>>

   // logout won't receive the refresh token because `AuthRepositoryImpl` already
   // has access to the tokens, so making the viewmodel also receive the tokens would
   // be repetitive
   suspend fun logout(): Flow<UiState<Unit>>

   suspend fun register(
      name: String,
      email: String,
      password: String,
      confirmPassword: String,
      deviceName: String
   ): Flow<UiState<Unit>>
}