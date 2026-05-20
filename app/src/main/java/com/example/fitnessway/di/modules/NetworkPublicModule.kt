package com.example.fitnessway.di.modules

import com.example.fitnessway.data.network.HttpClient
import com.example.fitnessway.data.network.createKtorHttpClient
import com.example.fitnessway.data.network.ktor_client.AuthApiClient
import org.koin.core.qualifier.named
import org.koin.dsl.module
import io.ktor.client.HttpClient as KtorHttpClient

private const val clientName = "publicKtorClient"

val networkPublicModule = module {
    single { HttpClient() }
    single<KtorHttpClient>(named(clientName)) { createKtorHttpClient(tokensStateHolder = get()) }
    single { AuthApiClient(client = get(named(clientName))) }
}
