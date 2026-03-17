package com.example.fitnessway.data.constants

import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesStoreKeys {
    val ACCESS_TOKEN = stringPreferencesKey("access_token")
    val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
}