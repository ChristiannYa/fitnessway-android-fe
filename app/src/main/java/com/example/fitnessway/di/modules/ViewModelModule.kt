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
            nutrientRepo = get(),
            foodRepo = get(),
            userStateHolder = get(),
            managers = get()
        )
    }

    viewModel {
        ListsViewModel(
            foodRepo = get(),
            managers = get(),
            userStateHolder = get()
        )
    }

    viewModel {
        ProfileViewModel(
            authRepo = get(),
            userStateHolder = get()
        )
    }
}