package com.example.fitnessway.di.modules

import com.example.fitnessway.data.state.token.TokenStateHolderImpl
import com.example.fitnessway.data.state.token.ITokensStateHolder
import com.example.fitnessway.data.state.user.IUserStateHolder
import com.example.fitnessway.data.state.user.UserStateHolderImpl
import com.example.fitnessway.di.ApplicationStateStoreImpl
import com.example.fitnessway.di.IApplicationStateStore
import org.koin.dsl.module

val stateModule = module {
   // Individual state holders...
   single<ITokensStateHolder> { TokenStateHolderImpl(get()) }
   single<IUserStateHolder> { UserStateHolderImpl() }

   // Aggregator
   single<IApplicationStateStore> {
      ApplicationStateStoreImpl(
         authStateHolder = get(),
         userStateHolder = get()
      )
   }
}