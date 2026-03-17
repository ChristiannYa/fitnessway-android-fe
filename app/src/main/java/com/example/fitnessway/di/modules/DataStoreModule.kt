package com.example.fitnessway.di.modules

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataStoreModule = module {
    single {
        androidContext().authTokensDataStore
    }
}

private val Context.authTokensDataStore by preferencesDataStore("auth_tokens")
