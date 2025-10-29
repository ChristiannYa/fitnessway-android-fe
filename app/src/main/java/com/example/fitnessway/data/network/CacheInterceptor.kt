package com.example.fitnessway.data.network

import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response

class CacheInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // Only handle GET requests
        if (request.method != "GET") return chain.proceed(request)

        // Try to use cache first
        val cacheRequest = request.newBuilder()
            .cacheControl(CacheControl.FORCE_CACHE)
            .build()

        val cacheResponse = chain.proceed(cacheRequest)

        // If cache miss (504), fetch from network
        val response = if (cacheResponse.code == 504) {
            cacheResponse.close() // Close the 504 response

            val networkResponse = chain.proceed(request)

            // Add cache headers to network response
            networkResponse.newBuilder()
                .header("Cache-Control", "public, max-age=1800")
                .removeHeader("Pragma")
                .build()
        } else cacheResponse

        return response
    }
}