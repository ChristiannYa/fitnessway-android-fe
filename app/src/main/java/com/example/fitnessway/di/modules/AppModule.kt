package com.example.fitnessway.di.modules

import com.example.fitnessway.di.init.AppInitializer
import org.koin.dsl.module

val appModule = module {
    single {
        AppInitializer(
            tokensStetHolder = get(),
            userStateHolder = get(),
            userRepo = get()
        )
    }
}