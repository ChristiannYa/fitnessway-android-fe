package com.example.fitnessway.di.modules

import com.example.fitnessway.data.network.createKtorHttpClient
import com.example.fitnessway.data.network.ktor_client.AppEdibleApiClient
import com.example.fitnessway.data.network.ktor_client.EdibleLogApiClient
import com.example.fitnessway.data.network.ktor_client.EdibleRecentLogApiClient
import com.example.fitnessway.data.network.ktor_client.NutrientApiClient
import com.example.fitnessway.data.network.ktor_client.NutrientIntakeApiClient
import com.example.fitnessway.data.network.ktor_client.PendingEdibleApiClient
import com.example.fitnessway.data.network.ktor_client.UserApiClient
import com.example.fitnessway.data.network.ktor_client.UserEdibleApiClient
import org.koin.core.qualifier.named
import org.koin.dsl.module
import io.ktor.client.HttpClient as KtorHttpClient

private const val clientName = "privateKtorClient"

val networkPrivateModule = module {
    single<KtorHttpClient>(named(clientName)) { createKtorHttpClient(tokensStateHolder = get()) }
    single { UserApiClient(client = get(named(clientName))) }
    single { NutrientApiClient(client = get(named(clientName))) }
    single { NutrientIntakeApiClient(client = get(named(clientName))) }
    single { AppEdibleApiClient(client = get(named(clientName))) }
    single { PendingEdibleApiClient(client = get(named(clientName))) }
    single { UserEdibleApiClient(client = get(named(clientName))) }
    single { EdibleLogApiClient(client = get(named(clientName))) }
    single { EdibleRecentLogApiClient(client = get(named(clientName))) }
}