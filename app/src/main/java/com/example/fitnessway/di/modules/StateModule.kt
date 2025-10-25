package com.example.fitnessway.di.modules

import com.example.fitnessway.data.state.AuthStateHolderImpl
import com.example.fitnessway.data.state.IAuthStateHolder
import com.example.fitnessway.di.ApplicationStateStoreImpl
import com.example.fitnessway.di.IApplicationStateStore
import org.koin.dsl.module

val stateModule = module {
   // Individual state holders
   single<IAuthStateHolder> { AuthStateHolderImpl(get()) }

   // Aggregator
   single<IApplicationStateStore> {
      ApplicationStateStoreImpl(
         authStateHolder = get()
      )
   }
}