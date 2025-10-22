package com.example.fitnessway.di.modules

import com.example.fitnessway.feature.welcome.screen.login.viewmodel.LoginViewModel
import org.koin.dsl.module
import org.koin.core.module.dsl.*

val viewModelModule = module {
   viewModel { LoginViewModel(repo = get()) }
   // viewModel { RegisterViewModel(repo = get()) }
}