package com.example.fitnessway.di.modules

import com.example.fitnessway.data.repository.RepositoryOperations
import org.koin.dsl.module

val repositoryOperationsModule = module {
    single {
        RepositoryOperations(
            userRepo = get(),
            userFoodRepo = get(),
            userSupplementRepo = get(),
            edibleLogRepo = get(),
            foodRecentLogRepo = get(),
            supplementRecentLogRepo = get(),
            nutrientRepo = get(),
            nutrientIntakesRepo = get(),
            pendingFoodRepo = get(),
            pendingSupplementRepository = get()
        )
    }
}