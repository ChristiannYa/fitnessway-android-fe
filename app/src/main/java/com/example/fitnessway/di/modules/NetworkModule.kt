package com.example.fitnessway.di.modules

import com.example.fitnessway.data.network.AuthInterceptor
import com.example.fitnessway.data.network.IAuthApiAuthorizedService
import com.example.fitnessway.data.network.IAuthApiService
import com.example.fitnessway.data.network.TokenAuthenticator
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

private const val BASE_URL = "http://10.0.2.2:8080/api/"
private const val AUTH_YES = "authYes"
private const val AUTH_NO = "authNo"

val networkModule = module {
   // Provide AuthInterceptor
   single<AuthInterceptor> {
      AuthInterceptor(authStateHolder = get())
   }

   // Provide TokenAuthenticator
   single<TokenAuthenticator> {
      TokenAuthenticator(
         authStateHolder = get(),
         baseUrl = BASE_URL
      )
   }

   // Retrofit WITHOUT authentication
   single<Retrofit>(named(AUTH_NO)) {
      val json = Json { ignoreUnknownKeys = true }
      val contentType = "application/json".toMediaType()

      Retrofit.Builder()
         .baseUrl(BASE_URL)
         .addConverterFactory(json.asConverterFactory(contentType))
         .build()
   }

   // Retrofit WITH authentication
   single<Retrofit>(named(AUTH_YES)) {
      val json = Json { ignoreUnknownKeys = true }
      val contentType = "application/json".toMediaType()

      val authenticatedClient = OkHttpClient.Builder()
         .authenticator(get<TokenAuthenticator>())
         .addInterceptor(get<AuthInterceptor>())
         .build()

      Retrofit.Builder()
         .baseUrl(BASE_URL)
         .client(authenticatedClient)
         .addConverterFactory(json.asConverterFactory(contentType))
         .build()
   }

   single<IAuthApiService> {
      get<Retrofit>(
         named(AUTH_NO)
      ).create(IAuthApiService::class.java)
   }

   single<IAuthApiAuthorizedService> {
      get<Retrofit>(
         named(AUTH_YES)
      ).create(IAuthApiAuthorizedService::class.java)
   }
}