package com.example.fitnessway.data.network

import com.example.fitnessway.data.state.auth.IAuthStateHolder
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
   private val authStateHolder: IAuthStateHolder
) : Interceptor {
   override fun intercept(chain: Interceptor.Chain): Response {
      // Get the request created by Retrofit
      val request = chain.request()

      val accessToken = authStateHolder.authState.value.accessToken

      // Modify the request and add the authorization header
      val newRequest = if (accessToken != null) {
         request.newBuilder()
            .header(
               "Authorization",
               "Bearer $accessToken"
            )
            .build()
      } else request

      return chain.proceed(newRequest)
   }
}