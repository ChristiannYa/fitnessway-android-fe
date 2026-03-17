package com.example.fitnessway.di.modules

import com.example.fitnessway.data.network.createKtorHttpClient
import com.example.fitnessway.data.network.ktor_client.AuthApiClient
import com.example.fitnessway.data.network.ktor_client.FoodApiClient
import com.example.fitnessway.data.network.ktor_client.NutrientApiClient
import com.example.fitnessway.data.network.ktor_client.UserApiClient
import org.koin.dsl.module
import io.ktor.client.HttpClient as KtorHttpClient

val networkModule = module {
    single<KtorHttpClient> {
        createKtorHttpClient(
            tokensStateHolder = get()
        )
    }

    single { UserApiClient(client = get()) }
    single { AuthApiClient(client = get()) }
    single { NutrientApiClient(client = get()) }
    single { FoodApiClient(client = get()) }
}
