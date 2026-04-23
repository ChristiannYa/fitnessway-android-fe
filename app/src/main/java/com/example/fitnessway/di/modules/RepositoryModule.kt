package com.example.fitnessway.di.modules

import com.example.fitnessway.data.repository.app_food.AppFoodRepositoryImpl
import com.example.fitnessway.data.repository.app_food.IAppFoodRepository
import com.example.fitnessway.data.repository.auth.AuthRepositoryImpl
import com.example.fitnessway.data.repository.auth.IAuthRepository
import com.example.fitnessway.data.repository.food_log.FoodLogRepositoryImpl
import com.example.fitnessway.data.repository.food_log.IFoodLogRepository
import com.example.fitnessway.data.repository.nutrient.INutrientRepository
import com.example.fitnessway.data.repository.nutrient.NutrientRepositoryImpl
import com.example.fitnessway.data.repository.pending_food.IPendingFoodRepository
import com.example.fitnessway.data.repository.pending_food.PendingFoodRepositoryImpl
import com.example.fitnessway.data.repository.user.IUserRepository
import com.example.fitnessway.data.repository.user.UserRepositoryImpl
import com.example.fitnessway.data.repository.user_food.IUserFoodRepository
import com.example.fitnessway.data.repository.user_food.UserFoodRepositoryImpl
import com.example.fitnessway.data.repository.user_supplement.IUserSupplementRepository
import com.example.fitnessway.data.repository.user_supplement.UserSupplementRepositoryImpl
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

    single<IAppFoodRepository> {
        AppFoodRepositoryImpl(
            httpClient = get(),
            apiClient = get(),
            repositoryScope = get(named("repositoryScope"))
        )
    }

    single<IPendingFoodRepository> {
        PendingFoodRepositoryImpl(
            httpClient = get(),
            apiClient = get(),
            repositoryScope = get(named("repositoryScope"))
        )
    }

    single<IUserFoodRepository> {
        UserFoodRepositoryImpl(
            apiClient = get(),
            httpClient = get(),
            repositoryScope = get(named("repositoryScope"))
        )
    }

    single<IUserSupplementRepository> {
        UserSupplementRepositoryImpl(
            apiClient = get(),
            httpClient = get(),
            repositoryScope = get(named("repositoryScope"))
        )
    }

    single<IFoodLogRepository> {
        FoodLogRepositoryImpl(
            apiClient = get(),
            httpClient = get(),
            repositoryScope = get(named("repositoryScope"))
        )
    }
}