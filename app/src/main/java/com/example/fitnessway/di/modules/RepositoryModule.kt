package com.example.fitnessway.di.modules

import com.example.fitnessway.data.repository.auth.AuthRepositoryImpl
import com.example.fitnessway.data.repository.auth.IAuthRepository
import com.example.fitnessway.data.repository.food.FoodRepositoryImpl
import com.example.fitnessway.data.repository.food.IFoodRepository
import com.example.fitnessway.data.repository.nutrient.INutrientRepository
import com.example.fitnessway.data.repository.nutrient.NutrientRepositoryImpl
import com.example.fitnessway.data.repository.user.IUserRepository
import com.example.fitnessway.data.repository.user.UserRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    single<IAuthRepository> {
        AuthRepositoryImpl(
            authApiService = get(),
            authApiAuthorizedService = get(),
            tokensStateHolder = get()
        )
    }

    single<IUserRepository> {
        UserRepositoryImpl(apiService = get())
    }

    single<INutrientRepository> {
        NutrientRepositoryImpl(apiService = get())
    }

    single<IFoodRepository> {
        FoodRepositoryImpl(apiService = get())
    }
}