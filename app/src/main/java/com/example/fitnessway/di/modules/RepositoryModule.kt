package com.example.fitnessway.di.modules

import com.example.fitnessway.data.repository.app_food.AppFoodRepositoryImpl
import com.example.fitnessway.data.repository.app_food.IAppFoodRepository
import com.example.fitnessway.data.repository.auth.AuthRepositoryImpl
import com.example.fitnessway.data.repository.auth.IAuthRepository
import com.example.fitnessway.data.repository.edible_list.food.IUserFoodRepository
import com.example.fitnessway.data.repository.edible_list.food.UserFoodRepositoryImpl
import com.example.fitnessway.data.repository.edible_list.supplement.IUserSupplementRepository
import com.example.fitnessway.data.repository.edible_list.supplement.UserSupplementRepositoryImpl
import com.example.fitnessway.data.repository.edible_log.EdibleLogRepositoryImpl
import com.example.fitnessway.data.repository.edible_log.IEdibleLogRepository
import com.example.fitnessway.data.repository.edible_recent_log.food.FoodRecentLogImpl
import com.example.fitnessway.data.repository.edible_recent_log.food.IFoodRecentLog
import com.example.fitnessway.data.repository.edible_recent_log.supplement.ISupplementRecentLog
import com.example.fitnessway.data.repository.edible_recent_log.supplement.SupplementRecentLogImpl
import com.example.fitnessway.data.repository.nutrient.INutrientRepository
import com.example.fitnessway.data.repository.nutrient.NutrientRepositoryImpl
import com.example.fitnessway.data.repository.pending.food.IPendingFoodRepository
import com.example.fitnessway.data.repository.pending.food.PendingFoodRepository
import com.example.fitnessway.data.repository.pending.supplement.IPendingSupplementRepository
import com.example.fitnessway.data.repository.pending.supplement.PendingSupplementRepository
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

    single<IAppFoodRepository> {
        AppFoodRepositoryImpl(
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

    single<IPendingFoodRepository> {
        PendingFoodRepository(
            httpClient = get(),
            apiClient = get(),
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

    single<IPendingSupplementRepository> {
        PendingSupplementRepository(
            apiClient = get(),
            httpClient = get(),
            repositoryScope = get(named("repositoryScope"))
        )
    }

    single<IEdibleLogRepository> {
        EdibleLogRepositoryImpl(
            apiClient = get(),
            httpClient = get(),
            repositoryScope = get(named("repositoryScope"))
        )
    }

    single<IFoodRecentLog> {
        FoodRecentLogImpl(
            apiClient = get(),
            httpClient = get(),
            repositoryScope = get(named("repositoryScope"))
        )
    }

    single<ISupplementRecentLog> {
        SupplementRecentLogImpl(
            apiClient = get(),
            httpClient = get(),
            repositoryScope = get(named("repositoryScope"))
        )
    }
}