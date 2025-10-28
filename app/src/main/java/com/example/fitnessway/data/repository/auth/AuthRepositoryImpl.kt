package com.example.fitnessway.data.repository.auth

import com.example.fitnessway.data.model.auth.LoginApiPostResponse
import com.example.fitnessway.data.model.auth.LoginRequest
import com.example.fitnessway.data.model.auth.LogoutRequest
import com.example.fitnessway.data.model.auth.RegisterRequest
import com.example.fitnessway.data.network.auth.IAuthApiAuthorizedService
import com.example.fitnessway.data.network.auth.IAuthApiService
import com.example.fitnessway.data.state.IAuthStateHolder
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.json.Json

class AuthRepositoryImpl(
   private val authApiService: IAuthApiService,
   private val authApiAuthorizedService: IAuthApiAuthorizedService,
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

         if (response.isSuccessful) {
            val body = response.body()

            if (body?.data != null) {
               authStateHolder.setAuth(
                  // Success is only true when the api returns both tokens, so we are
                  // use assert operators because we are sure that both tokens are present
                  refreshToken = body.data.refreshToken,
                  accessToken = body.data.accessToken
               )
               emit(UiState.Success(Unit))
            } else {
               // Handle edge cases when there is a dependency error e.g., Retrofit bug,
               // Serialization fail
               emit(UiState.Error("Login failed"))
            }
         } else { // HTTP 400/401/500 error codes
            val errMsg = parseLoginErrorBody(response.errorBody()?.string()) // Debug
            if (errMsg == "invalid email or password") {
               emit(UiState.Error(errMsg))
            } else {
               emit(UiState.Error("Login failed"))
            }
         }
      } catch (e: Exception) {
         // Network errors
         emit(UiState.Error("Login network error"))
      }
   }.flowOn(Dispatchers.IO)

   override suspend fun logout(): Flow<UiState<Unit>> = flow {
      emit(UiState.Loading)

      val refreshToken = authStateHolder.authState.value.refreshToken

      if (refreshToken == null) {
         authStateHolder.clearAuth()
         emit(UiState.Success(Unit))
         // complete `flow {` lambda above
         // The code below this block won't be called
         return@flow
      }

      try {
         val response = authApiAuthorizedService.logout(
            LogoutRequest(refreshToken)
         )

         if (response.isSuccessful) {
            val body = response.body()

            if (body?.success == true) {
               authStateHolder.clearAuth()
               emit(UiState.Success(Unit))
            } else {
               // Still clear auth even if API returns an error
               authStateHolder.clearAuth()
               emit(UiState.Error("Logout failed"))
            }
         } else {
            // Still clear auth even on HTTP error
            authStateHolder.clearAuth()
            // val errMsg = response.message()
            emit(UiState.Error("Logout failed"))
         }
      } catch (e: Exception) {
         // Still clear auth even on network error
         authStateHolder.clearAuth()
         emit(UiState.Error("Logout network error"))
      }
   }.flowOn(Dispatchers.IO)

   override suspend fun register(
      name: String,
      email: String,
      password: String,
      confirmPassword: String,
      deviceName: String
   ): Flow<UiState<Unit>> = flow {
      emit(UiState.Loading)

      try {
         val response = authApiService.register(
            RegisterRequest(
               name,
               email,
               password,
               confirmPassword,
               deviceName
            )
         )

         when {
            response.code() == 409 -> {
               emit(UiState.Error("This email is already in use"))
            }

            response.isSuccessful -> {
               val body = response.body()

               if (body?.data != null) {
                  authStateHolder.setAuth(
                     refreshToken = body.data.refreshToken,
                     accessToken = body.data.accessToken
                  )
                  emit(UiState.Success(Unit))
               } else {
                  emit(UiState.Error("Registration failed"))
               }
            }

            else -> {
               // val errMsg = response.errorBody()?.string()
               emit(UiState.Error("Registration failed"))
            }
         }
      } catch (e: Exception) {
         emit(UiState.Error("Registration network error"))
      }
   }.flowOn(Dispatchers.IO)
}

private fun parseLoginErrorBody(errorBody: String?): String {
   if (errorBody == null) return "Login failed"

   return try {
      val json = Json { ignoreUnknownKeys = true }
      val errorResponse = json.decodeFromString<LoginApiPostResponse>(errorBody)

      if (!errorResponse.errors.isNullOrEmpty()) {
         errorResponse.errors.values.joinToString("\n• ", prefix = "• ")
      } else {
         errorResponse.message as String
      }
   } catch (e: Exception) {
      "Login failed"
   }
}