package com.example.fitnessway.di.modules

import com.example.fitnessway.data.network.interceptors.AuthInterceptor
import com.example.fitnessway.data.network.interceptors.CacheInterceptor
import com.example.fitnessway.data.network.auth.IAuthApiAuthorizedService
import com.example.fitnessway.data.network.auth.IAuthApiService
import com.example.fitnessway.data.network.TokenAuthenticator
import com.example.fitnessway.data.network.nutrient.INutrientApiService
import com.example.fitnessway.data.network.user.IUserApiService
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.io.File

private const val BASE_URL = "http://10.0.0.4:8080/api/"
private const val AUTH_YES = "authYes"
private const val AUTH_NO = "authNo"

val networkModule = module {
    // Provide CacheInterceptor
    single<CacheInterceptor> {
        CacheInterceptor()
    }

    // Provide Cache
    single<Cache> {
        val cacheSize = 10 * 1024 * 1024L // 10 MB
        val cacheDir = File(
            androidContext().cacheDir,
            "okhttp_cache"
        )

        Cache(directory = cacheDir, maxSize = cacheSize)
    }

    // Provide AuthInterceptor
    single<AuthInterceptor> {
        AuthInterceptor(tokensStateHolder = get())
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

        // @NOTE
        // Cache interceptor should come before auth interceptor so that it can add cache headers
        // before the auth token is added

        val authenticatedClient = OkHttpClient.Builder()
            .cache(get<Cache>())
            .authenticator(get<TokenAuthenticator>())
            .addInterceptor(get<CacheInterceptor>())
            .addInterceptor(get<AuthInterceptor>())
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(authenticatedClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    // For welcoming users to either login or register
    single<IAuthApiService> {
        get<Retrofit>(
            named(AUTH_NO)
        ).create(IAuthApiService::class.java)
    }

    // For users who are already logged in
    single<IAuthApiAuthorizedService> {
        get<Retrofit>(
            named(AUTH_YES)
        ).create(IAuthApiAuthorizedService::class.java)
    }

    // User related data
    single<IUserApiService> {
        get<Retrofit>(
            named(AUTH_YES)
        ).create(IUserApiService::class.java)
    }

    // Nutrient related data
    single<INutrientApiService> {
        get<Retrofit>(
            named(AUTH_YES)
        ).create(INutrientApiService::class.java)
    }
}