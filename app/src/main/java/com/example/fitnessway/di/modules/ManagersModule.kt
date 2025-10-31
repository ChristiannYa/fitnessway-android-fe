package com.example.fitnessway.di.modules

import com.example.fitnessway.feature.home.manager.DateManager
import com.example.fitnessway.feature.home.manager.FoodLogManager
import com.example.fitnessway.feature.home.manager.HomeManagersImpl
import com.example.fitnessway.feature.home.manager.IHomeManagers
import org.koin.dsl.module

val managersModule = module {
    single<IHomeManagers> {
        HomeManagersImpl(
            date = DateManager(),
            foodLog = FoodLogManager()
        )
    }
}