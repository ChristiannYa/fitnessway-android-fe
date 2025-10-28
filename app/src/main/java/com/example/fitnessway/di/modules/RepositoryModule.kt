package com.example.fitnessway.di.modules

import com.example.fitnessway.data.repository.auth.AuthRepositoryImpl
import com.example.fitnessway.data.repository.auth.IAuthRepository
import com.example.fitnessway.data.repository.nutrient.INutrientRepository
import com.example.fitnessway.data.repository.nutrient.NutrientRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    single<IAuthRepository> {
        AuthRepositoryImpl(
            authApiService = get(),
            authApiAuthorizedService = get(),
            authStateHolder = get()
        )
    }

    single<INutrientRepository> {
        NutrientRepositoryImpl(apiService = get())
    }
}