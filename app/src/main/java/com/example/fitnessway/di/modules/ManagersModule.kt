package com.example.fitnessway.di.modules

import com.example.fitnessway.feature.home.manager.date.DateManager
import com.example.fitnessway.feature.home.manager.foodlog.FoodLogManager
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