package com.example.fitnessway.feature.profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.fitnessway.feature.profile.screen.main.ProfileScreen
import com.example.fitnessway.feature.profile.screen.settings.ProfileSettingsScreen
import com.example.navigation.ProfileGraph
import kotlinx.serialization.Serializable

@Serializable
object ProfileMain

@Serializable
private object ProfileSettings

fun NavGraphBuilder.profileNavigationGraph(navController: NavController) {
   navigation<ProfileGraph>(startDestination = ProfileMain) {
      composable<ProfileMain> {
         ProfileScreen(
            onSettings = {
               navController.navigate(ProfileSettings)
            }
         )
      }

      composable<ProfileSettings> {
         ProfileSettingsScreen(
            onBackClick = {
               navController.popBackStack()
            }
         )
      }
   }
}