package com.example.fitnessway.di.modules

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.example.fitnessway.data.serializer.AuthTokensSerializer
import com.fitnessway.data.AuthTokens
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataStoreModule = module {
    single<DataStore<AuthTokens>> {
        DataStoreFactory.create(
            serializer = AuthTokensSerializer,
            produceFile = { androidContext().dataStoreFile("auth_tokens.pb") }
        )
    }

    // Example when creating another DataStore
    // `named("settings")` is included because there will be more than one DataStore
    /*
    // User settings
    single<DataStore<UserSettings>>(named("settings")) {
        DataStoreFactory.create(
            serializer = UserSettingsSerializer,
            produceFile = { androidContext().dataStoreFile("user_settings.pb") }
        )
    }
     */
}