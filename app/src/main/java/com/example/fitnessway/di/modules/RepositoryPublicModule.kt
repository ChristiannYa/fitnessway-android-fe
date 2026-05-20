package com.example.fitnessway.di.modules

import com.example.fitnessway.data.repository.auth.AuthRepositoryImpl
import com.example.fitnessway.data.repository.auth.IAuthRepository
import org.koin.dsl.module

val repositoryPublicModule = module {
    single<IAuthRepository> {
        AuthRepositoryImpl(
            apiClient = get(),
            httpClient = get(),
            tokensStateHolder = get()
        )
    }
}