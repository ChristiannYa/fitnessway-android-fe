package com.example.fitnessway.di.modules

import com.example.fitnessway.di.init.AppInitializer
import org.koin.dsl.module

val appModule = module {
    single {
        AppInitializer(
            tokensStateHolder = get(),
            userStateHolder = get(),
            userRepo = get(),
            cacheManager = get()
        )
    }
}