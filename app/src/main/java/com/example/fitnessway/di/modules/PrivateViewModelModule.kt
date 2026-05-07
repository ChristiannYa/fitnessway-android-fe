package com.example.fitnessway.di.modules

import com.example.fitnessway.feature.home.viewmodel.HomeViewModel
import com.example.fitnessway.feature.lists.viewmodel.ListsViewModel
import com.example.fitnessway.feature.profile.viewmodel.ProfileViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val privateViewModelModule = module {
    viewModel {
        HomeViewModel(
            userRepo = get(),
            appFoodRepo = get(),
            nutrientRepo = get(),
            userFoodRepo = get(),
            userSupplementRepo = get(),
            foodLogRepo = get(),
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
            foodRepo = get(),
            foodLogRepo = get(),
            managers = get(),
            dateTimeFormatter = get(),
        )
    }
}