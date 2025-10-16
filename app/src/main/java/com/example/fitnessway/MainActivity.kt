package com.example.fitnessway

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.fitnessway.ui.theme.FitnesswayTheme
import com.example.navigation.AppNavigation

class MainActivity : ComponentActivity() {
   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      enableEdgeToEdge()

      setContent {
         FitnesswayTheme {
            AppNavigation()
         }
      }
   }
}

// Link to know preferable versions (libs.versions.toml)
// https://github.com/android/nowinandroid/blob/main/gradle/libs.versions.toml