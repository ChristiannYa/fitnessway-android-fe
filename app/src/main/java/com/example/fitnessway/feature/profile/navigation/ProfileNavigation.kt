package com.example.fitnessway.feature.profile.navigation

import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.fitnessway.feature.profile.screen.accinfo.ProfileAccountInformationScreen
import com.example.fitnessway.feature.profile.screen.colors.ProfileColorsScreen
import com.example.fitnessway.feature.profile.screen.goals.ProfileGoalsScreen
import com.example.fitnessway.feature.profile.screen.main.ProfileScreen
import com.example.fitnessway.feature.profile.screen.settings.ProfileSettingsScreen
import com.example.fitnessway.feature.profile.viewmodel.ProfileViewModel
import com.example.fitnessway.navigation.ProfileGraph
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
object ProfileMain

@Serializable
object ProfileGoalsDest

@Serializable
private object ProfileColorsDest

@Serializable
private object ProfileAccountInformationDest

@Serializable
private object ProfileSettingsDest

fun NavGraphBuilder.profileNavigationGraph(navController: NavController) {
    navigation<ProfileGraph>(startDestination = ProfileMain) {
        composable<ProfileMain> { entry ->
            val parentEntry = remember(entry) {
                navController.getBackStackEntry<ProfileGraph>()
            }

            val viewModel: ProfileViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

            ProfileScreen(
                viewModel = viewModel,
                onNavigateToGoals = { navController.navigate(ProfileGoalsDest) },
                onNavigateToColors = { navController.navigate(ProfileColorsDest) },
                onNavigateToAccInfo = { navController.navigate(ProfileAccountInformationDest) },
                onNavigateToSettings = { navController.navigate(ProfileSettingsDest) },
            )
        }

        composable<ProfileGoalsDest> { entry ->
            val parentEntry = remember(entry) {
                navController.getBackStackEntry<ProfileGraph>()
            }

            val viewModel: ProfileViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

            ProfileGoalsScreen(
                viewModel = viewModel,
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