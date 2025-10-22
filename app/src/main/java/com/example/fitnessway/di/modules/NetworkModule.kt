package com.example.fitnessway.di.modules

import com.example.fitnessway.data.network.IAuthApiService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

private const val BASE_URL = "http://10.0.2.2:8080/api/"

val networkModule = module {
   single<Retrofit> {
      val json = Json { ignoreUnknownKeys = true }
      val contentType = "application/json".toMediaType()

      Retrofit.Builder()
         .baseUrl(BASE_URL)
         .addConverterFactory(json.asConverterFactory(contentType))
         .build()
   }

   single<IAuthApiService> {
      get<Retrofit>().create(IAuthApiService::class.java)
   }
}