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
            authApiService = get(),
            authApiAuthorizedService = get(),
            tokensStateHolder = get()
        )
    }

    single<IUserRepository> {
        UserRepositoryImpl(
            apiService = get(),
            httpClient = get()
        )
    }

    single<INutrientRepository> {
        NutrientRepositoryImpl(
            httpClient = get(),
            apiService = get(),
            repositoryScope = get(named("repositoryScope")),
            cacheManager = get()
        )
    }

    single<IFoodRepository> {
        FoodRepositoryImpl(
            apiService = get(),
            httpClient = get(),
            repositoryScope = get(named("repositoryScope")),
            cacheManager = get()
        )
    }
}