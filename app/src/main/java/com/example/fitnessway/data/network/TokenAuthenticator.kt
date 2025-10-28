package com.example.fitnessway.data.network

import com.example.fitnessway.data.model.auth.RefreshTokenApiPostResponse
import com.example.fitnessway.data.model.auth.RefreshTokenRequest
import com.example.fitnessway.data.state.auth.IAuthStateHolder
import kotlinx.serialization.json.Json
import okhttp3.Authenticator
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
   private val authStateHolder: IAuthStateHolder,
   private val baseUrl: String,
) : Authenticator {

   override fun authenticate(
      route: Route?,
      response: Response
   ): Request? {
      synchronized(this) {
         val currentToken = authStateHolder.authState.value.accessToken
         val requestToken = response.request.header(
            "Authorization"
         )?.removePrefix("Bearer ")

         // Check if the current token has already been refreshed by another thread.
         // This happens when multiple simultaneous API calls receive 401 responses.
         // If another thread already refreshed the token, we can reuse it instead of
         // making another refresh request.
         if (currentToken != null && requestToken != currentToken) {
            return response.request.newBuilder()
               .header(
                  "Authorization",
                  "Bearer $currentToken"
               )
               .build()
         }

         // Get current refresh token or clear authentication if missing
         val refreshToken = authStateHolder.authState.value.refreshToken

         if (refreshToken == null) {
            authStateHolder.clearAuth()
            return null
         }

         // Attempt to refresh the token
         val newAccessToken = refreshToken(refreshToken)

         // Retry the same request with the new access token
         return if (newAccessToken != null) {
            response.request.newBuilder()
               .header(
                  "Authorization",
                  "Bearer $newAccessToken"
               )
               .build()
         } else {
            // Clear authentication if the token refresh failed
            authStateHolder.clearAuth()
            null
         }
      }
   }

   private fun refreshToken(refreshToken: String): String? {
      return try {
         val client = OkHttpClient()
         val json = Json { ignoreUnknownKeys = true }

         val refreshData = json.encodeToString(
            RefreshTokenRequest(refreshToken, "Android Device")
         ).toRequestBody("application/json".toMediaType())

         val request = Request.Builder()
            .url("${baseUrl}auth/refresh")
            .post(refreshData)
            .build()

         val httpResponse = client.newCall(request).execute()

         if (httpResponse.isSuccessful) {
            val httpResponseString = httpResponse.body?.string() ?: return null
            val response = json.decodeFromString<RefreshTokenApiPostResponse>(httpResponseString)

            val newAccessToken = response.data?.accessToken
            val newRefreshToken = response.data?.refreshToken

            // Store new tokens if the response was a success
            if (newAccessToken != null && newRefreshToken != null) {
               authStateHolder.setAuth(newAccessToken, newRefreshToken)
               newAccessToken
            } else null
         } else null
      } catch (e: Exception) {
         null
      }
   }
}