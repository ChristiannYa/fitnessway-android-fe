package com.example.fitnessway.di

import android.app.Application
import com.example.fitnessway.di.modules.networkModule
import com.example.fitnessway.di.modules.stateModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class FitnesswayApp : Application() {
   override fun onCreate() {
      super.onCreate()

      startKoin {
         androidContext(this@FitnesswayApp)
         androidLogger()
         modules(
            stateModule,
            networkModule
         )
      }
   }
}