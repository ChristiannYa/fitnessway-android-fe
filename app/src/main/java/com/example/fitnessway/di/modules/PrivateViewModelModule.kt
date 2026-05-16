package com.example.fitnessway.di.modules

import com.example.fitnessway.feature.home.viewmodel.HomeViewModel
import com.example.fitnessway.feature.lists.viewmodel.ListsViewModel
import com.example.fitnessway.feature.profile.viewmodel.ProfileViewModel
import com.example.fitnessway.feature.profile.viewmodel.operation.ProfileOperations
import com.example.fitnessway.feature.profile.viewmodel.operation.TimeZoneChangeOperation
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

// @TODO: Figure reduce the amount repositories passed in to every view model
val privateViewModelModule = module {
    viewModel {
        HomeViewModel(
            userRepo = get(),
            appFoodRepo = get(),
            nutrientRepo = get(),
            nutrientIntakesRepo = get(),
            userFoodRepo = get(),
            userSupplementRepo = get(),
            edibleLogRepo = get(),
            foodRecentLogRepo = get(),
            supplementRecentLogRepo = get(),
            managers = get(),
            dateTimeFormatter = get()
        )
    }

    viewModel {
        ListsViewModel(
            userRepo = get(),
            pendingFoodRepo = get(),
            pendingSupplementRepo = get(),
            userFoodRepo = get(),
            userSupplementRepo = get(),
            nutrientRepo = get(),
            foodLogRepo = get(),
            foodRecentLogRepo = get(),
            managers = get()
        )
    }

    viewModel {
        ProfileViewModel(
            authRepo = get(),
            userRepo = get(),
            nutrientRepo = get(),
            nutrientIntakesRepo = get(),
            userFoodRepo = get(),
            edibleLogRepo = get(),
            operations = ProfileOperations(
                timezoneChange = TimeZoneChangeOperation(
                    edibleLogRepo = get(),
                    foodRecentLogRepo = get(),
                    supplementRecentLogRepo = get(),
                    nutrientIntakeRepo = get()
                )
            ),
            managers = get(),
            timezoneStateHolder = get(),
            dateTimeFormatter = get(),
        )
    }
}