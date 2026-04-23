package com.example.fitnessway.di.modules

import com.example.fitnessway.feature.home.viewmodel.HomeViewModel
import com.example.fitnessway.feature.lists.viewmodel.ListsViewModel
import com.example.fitnessway.feature.profile.viewmodel.ProfileViewModel
import com.example.fitnessway.feature.welcome.screen.login.viewmodel.LoginViewModel
import com.example.fitnessway.feature.welcome.screen.register.viewmodel.RegisterViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        LoginViewModel(
            repo = get()
        )
    }

    viewModel {
        RegisterViewModel(
            repo = get()
        )
    }

    viewModel {
        HomeViewModel(
            appFoodRepo = get(),
            nutrientRepo = get(),
            userFoodRepo = get(),
            userSupplementRepo = get(),
            foodLogRepo = get(),
            managers = get(),
            appStateStore = get(),
            dateTimeFormatter = get()
        )
    }

    viewModel {
        ListsViewModel(
            pendingFoodRepo = get(),
            userFoodRepo = get(),
            userSupplementRepo = get(),
            nutrientRepo = get(),
            foodLogRepo = get(),
            managers = get(),
            userStateHolder = get()
        )
    }

    viewModel {
        ProfileViewModel(
            authRepo = get(),
            nutrientRepo = get(),
            foodRepo = get(),
            foodLogRepo = get(),
            managers = get(),
            dateTimeFormatter = get(),
            userStateHolder = get()
        )
    }
}