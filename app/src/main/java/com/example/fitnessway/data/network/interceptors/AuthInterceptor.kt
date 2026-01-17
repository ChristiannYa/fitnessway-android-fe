package com.example.fitnessway.data.network.interceptors

import com.example.fitnessway.data.state.token.ITokensStateHolder
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val tokensStateHolder: ITokensStateHolder
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        // Get the request created by Retrofit
        val request = chain.request()
        val accessToken = tokensStateHolder.tokensState.value.accessToken

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