package com.example.fitnessway.di

import android.app.Application
import com.example.fitnessway.di.init.AppInitializer
import com.example.fitnessway.di.modules.appModule
import com.example.fitnessway.di.modules.dataStoreModule
import com.example.fitnessway.di.modules.networkModule
import com.example.fitnessway.di.modules.repositoryModule
import com.example.fitnessway.di.modules.stateModule
import com.example.fitnessway.di.modules.viewModelModule
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class FitnesswayApp : Application() {
   private val appInitializer: AppInitializer by inject()

   override fun onCreate() {
      super.onCreate()

      startKoin {
         androidContext(this@FitnesswayApp)
         androidLogger()
         modules(
            appModule,
            stateModule,
            networkModule,
            viewModelModule,
            repositoryModule,
            dataStoreModule,
         )
      }

      appInitializer.initialize()
   }
}