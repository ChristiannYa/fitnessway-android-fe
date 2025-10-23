package com.example.fitnessway.di.modules

import com.example.fitnessway.data.repository.AuthRepositoryImpl
import com.example.fitnessway.data.repository.IAuthRepository
import org.koin.dsl.module

val repositoryModule = module {
   single<IAuthRepository> {
      AuthRepositoryImpl(
         authApiService = get(),
         authApiAuthorizedService = get(),
         authStateHolder = get()
      )
   }
}