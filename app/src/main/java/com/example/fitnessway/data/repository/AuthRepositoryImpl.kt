package com.example.fitnessway.data.repository

import android.util.Log
import com.example.fitnessway.data.model.auth.LoginApiPostResponse
import com.example.fitnessway.data.model.auth.LoginRequest
import com.example.fitnessway.data.network.IAuthApiService
import com.example.fitnessway.data.state.IAuthStateHolder
import com.example.fitnessway.util.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.json.Json

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

         if (response.isSuccessful) {
            val body = response.body()

            if (body?.content != null) {
               authStateHolder.setAuth(
                  // Success is only true when the api returns both tokens, so we are
                  // use assert operators because we are sure that both tokens are present
                  refreshToken = body.content.refreshToken,
                  accessToken = body.content.accessToken
               )
               emit(UiState.Success(Unit))
            } else {
               // Handle edge cases when there is a dependency error e.g., Retrofit bug,
               // Serialization fail
               Log.d("Fitnessway-Client", "Unexpected response: body=$body")
               emit(UiState.Error("Login failed"))
            }
         } else {
            // HTTP 400/401/500 error codes

            val errorMessage = parseErrorBody(response.errorBody()?.string())
            emit(UiState.Error(errorMessage))
         }
      } catch (e: Exception) {
         // Network errors
         emit(UiState.Error("Login network error: ${e.message}"))
      }
   }.flowOn(Dispatchers.IO)
}

private fun parseErrorBody(errorBody: String?): String {
   if (errorBody == null) {
      Log.d("Fitnessway-Client", "parseErrorBody: errorBody is null")
      return "Login failed"
   }

   return try {
      val json = Json { ignoreUnknownKeys = true }
      val errorResponse = json.decodeFromString<LoginApiPostResponse>(errorBody)

      if (!errorResponse.errors.isNullOrEmpty()) {
         errorResponse.errors.values.joinToString("\n• ", prefix = "• ")
      } else {
         val errorResponseMsg = errorResponse.message as String
         Log.d("Fitnessway-Client", "parseErrorBody: $errorResponseMsg")

         if (errorResponseMsg == "invalid email or password") {
            errorResponseMsg.replaceFirstChar {
               it.titlecase()
            }
         } else {
            "Login failed"
         }
      }
   } catch (e: Exception) {
      Log.d("Fitnessway-Client", "parseErrorBody: ${e.message}")
      "Login failed"
   }
}