package com.example.fitnessway.feature.profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.fitnessway.feature.profile.screen.accinfo.ProfileAccountInformationScreen
import com.example.fitnessway.feature.profile.screen.colors.ProfileColorsScreen
import com.example.fitnessway.feature.profile.screen.goals.ProfileGoalsScreen
import com.example.fitnessway.feature.profile.screen.main.ProfileScreen
import com.example.fitnessway.feature.profile.screen.settings.ProfileSettingsScreen
import com.example.fitnessway.navigation.ProfileGraph
import kotlinx.serialization.Serializable

@Serializable
object ProfileMain

@Serializable
private object ProfileGoalsDest

@Serializable
private object ProfileColorsDest

@Serializable
private object ProfileAccountInformationDest

@Serializable
private object ProfileSettingsDest

fun NavGraphBuilder.profileNavigationGraph(navController: NavController) {
    navigation<ProfileGraph>(startDestination = ProfileMain) {
        composable<ProfileMain> {
            ProfileScreen(
                onNavigateToGoals = { navController.navigate(ProfileGoalsDest) },
                onNavigateToColors = { navController.navigate(ProfileColorsDest) },
                onNavigateToAccInfo = { navController.navigate(ProfileAccountInformationDest) },
                onNavigateToSettings = { navController.navigate(ProfileSettingsDest) },
            )
        }

        composable<ProfileGoalsDest> {
            ProfileGoalsScreen(
                onBackClick = navController::popBackStack
            )
        }

        composable<ProfileColorsDest> {
            ProfileColorsScreen(
                onBackClick = navController::popBackStack
            )
        }

        composable<ProfileAccountInformationDest> {
            ProfileAccountInformationScreen(
                onBackClick = navController::popBackStack
            )
        }

        composable<ProfileSettingsDest> {
            ProfileSettingsScreen(
                onBackClick = navController::popBackStack
            )
        }
    }
}