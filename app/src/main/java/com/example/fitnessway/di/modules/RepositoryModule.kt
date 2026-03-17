package com.example.fitnessway.di.modules

import com.example.fitnessway.data.repository.auth.AuthRepositoryImpl
import com.example.fitnessway.data.repository.auth.IAuthRepository
import com.example.fitnessway.data.repository.food.FoodRepositoryImpl
import com.example.fitnessway.data.repository.food.IFoodRepository
import com.example.fitnessway.data.repository.nutrient.INutrientRepository
import com.example.fitnessway.data.repository.nutrient.NutrientRepositoryImpl
import com.example.fitnessway.data.repository.user.IUserRepository
import com.example.fitnessway.data.repository.user.UserRepositoryImpl
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModule = module {
    single<IAuthRepository> {
        AuthRepositoryImpl(
            apiClient = get(),
            httpClient = get(),
            tokensStateHolder = get(),
            userStateHolder = get()
        )
    }

    single<IUserRepository> {
        UserRepositoryImpl(
            apiClient = get(),
            httpClient = get()
        )
    }

    single<INutrientRepository> {
        NutrientRepositoryImpl(
            httpClient = get(),
            apiClient = get(),
            repositoryScope = get(named("repositoryScope"))
        )
    }

    single<IFoodRepository> {
        FoodRepositoryImpl(
            apiClient = get(),
            httpClient = get(),
            repositoryScope = get(named("repositoryScope"))
        )
    }
}