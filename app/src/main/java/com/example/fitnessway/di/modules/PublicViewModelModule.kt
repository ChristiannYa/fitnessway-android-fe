package com.example.fitnessway.di.modules

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
}