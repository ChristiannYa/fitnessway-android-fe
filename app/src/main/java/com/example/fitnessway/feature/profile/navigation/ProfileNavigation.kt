package com.example.fitnessway.feature.profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.fitnessway.feature.profile.screen.goals.ProfileGoalsScreen
import com.example.fitnessway.feature.profile.screen.main.ProfileScreen
import com.example.fitnessway.feature.profile.screen.settings.ProfileSettingsScreen
import com.example.fitnessway.navigation.ProfileGraph
import kotlinx.serialization.Serializable

@Serializable
object ProfileMain

@Serializable
private object ProfileGoals

@Serializable
private object ProfileSettings

fun NavGraphBuilder.profileNavigationGraph(navController: NavController) {
    navigation<ProfileGraph>(startDestination = ProfileMain) {
        composable<ProfileMain> {
            ProfileScreen(
                onSettings = { navController.navigate(ProfileSettings) },
                onGoals = { navController.navigate(ProfileGoals) }
            )
        }

        composable<ProfileGoals> {
            ProfileGoalsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<ProfileSettings> {
            ProfileSettingsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}