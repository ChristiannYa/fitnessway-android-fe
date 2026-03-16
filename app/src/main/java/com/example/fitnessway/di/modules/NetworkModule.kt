package com.example.fitnessway.di.modules

import com.example.fitnessway.data.network.ApiUrls
import com.example.fitnessway.data.network.CacheManager
import com.example.fitnessway.data.network.HttpClient
import com.example.fitnessway.data.network.interceptors.AuthInterceptor
import com.example.fitnessway.data.network.interceptors.CacheInterceptor
import com.example.fitnessway.data.network.RetrofitService.IAuthorized
import com.example.fitnessway.data.network.RetrofitService.IUnauthorized
import com.example.fitnessway.data.network.TokenAuthenticator
import com.example.fitnessway.data.network.RetrofitService.IFoodApiService
import com.example.fitnessway.data.network.RetrofitService.INutrientApiService
import com.example.fitnessway.data.network.RetrofitService.IUser
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
import com.example.fitnessway.data.network.ApiUrls.BASE_URL_GO

private const val AUTH_YES = "authYes"
private const val AUTH_NO = "authNo"

val networkModule = module {
    // Provide CacheInterceptor
    single<CacheInterceptor> {
        CacheInterceptor()
    }

    // Provide CacheManager
    single<CacheManager> {
        CacheManager(cache = get())
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
            baseUrl = ApiUrls.BASE_URL_KT
        )
    }

    // Retrofit WITHOUT authentication
    single<Retrofit>(named(AUTH_NO)) {
        val json = Json { ignoreUnknownKeys = true }
        val contentType = "application/json".toMediaType()

        Retrofit.Builder()
            .baseUrl(BASE_URL_GO)
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
            .baseUrl(BASE_URL_GO)
            .client(authenticatedClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    single<HttpClient> {
        HttpClient(cacheManager = get())
    }

    // For welcoming users to either login or register
    single<IUnauthorized> {
        get<Retrofit>(
            named(AUTH_NO)
        ).create(IUnauthorized::class.java)
    }

    // For users who are already logged in
    single<IAuthorized> {
        get<Retrofit>(
            named(AUTH_YES)
        ).create(IAuthorized::class.java)
    }

    // User related
    single<IUser> {
        get<Retrofit>(
            named(AUTH_YES)
        ).create(IUser::class.java)
    }

    // Nutrient related
    single<INutrientApiService> {
        get<Retrofit>(
            named(AUTH_YES)
        ).create(INutrientApiService::class.java)
    }

    // Food related
    single<IFoodApiService> {
        get<Retrofit>(
            named(AUTH_YES)
        ).create(IFoodApiService::class.java)
    }
}