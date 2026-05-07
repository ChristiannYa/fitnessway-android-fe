package com.example.fitnessway.di.modules

import com.example.fitnessway.data.state.AppStateStoreImpl
import com.example.fitnessway.data.state.IAppStateStore
import com.example.fitnessway.data.state.token.ITokensStateHolder
import com.example.fitnessway.data.state.token.TokenStateHolderImpl
import org.koin.dsl.module

val stateModule = module {
    // Individual state holders
    single<ITokensStateHolder> { TokenStateHolderImpl(get()) }

    // Aggregator
    single<IAppStateStore> {
        AppStateStoreImpl(
            tokensStateHolder = get(),
        )
    }
}