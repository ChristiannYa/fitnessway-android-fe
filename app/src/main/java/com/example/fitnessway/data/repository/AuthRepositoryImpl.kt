package com.example.fitnessway.data.repository

import com.example.fitnessway.data.model.auth.LoginRequest
import com.example.fitnessway.data.network.IAuthApiService
import com.example.fitnessway.data.state.IAuthStateHolder
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class AuthRepositoryImpl(
   private val authApiService: IAuthApiService,
   private val authStateHolder: IAuthStateHolder
) : IAuthRepository {
   override suspend fun login(
      email: String,
      password: String,
      deviceName: String
   ): Flow<UiState<Unit>> = flow {
      emit(UiState.Loading)

      try {
         val response = authApiService.login(
            LoginRequest(email, password, deviceName)
         )
         if (response.success && response.content != null) {
            authStateHolder.setAuth(
               accessToken = response.content.accessToken,
               refreshToken = response.content.refreshToken
            )

            emit(UiState.Success(Unit))
         } else {
            emit(UiState.Error(response.message))
         }
      } catch (e: Exception) {
         emit(UiState.Error("Network error when logging in: ${e.message}"))
      }
   }.flowOn(Dispatchers.IO)
}