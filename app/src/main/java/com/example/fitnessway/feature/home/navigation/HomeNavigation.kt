package com.example.fitnessway.feature.home.navigation

import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.fitnessway.feature.home.screen.foodselection.FoodSelectionScreen
import com.example.fitnessway.feature.home.screen.foodselection.food_request.FoodRequestScreen
import com.example.fitnessway.feature.home.screen.foodselection.foodlog.FoodLogScreen
import com.example.fitnessway.feature.home.screen.logdetails.FoodLogDetailsScreen
import com.example.fitnessway.feature.home.screen.main.HomeScreen
import com.example.fitnessway.feature.home.viewmodel.HomeViewModel
import com.example.fitnessway.feature.profile.navigation.ProfileGoalsDest
import com.example.fitnessway.navigation.HomeGraph
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
object HomeMainDest

@Serializable
private object FoodSelection

@Serializable
private object FoodRequest

@Serializable
private object FoodLogDetails

@Serializable
private object FoodLog

fun NavGraphBuilder.homeNavigationGraph(navController: NavController) {
    navigation<HomeGraph>(startDestination = HomeMainDest) {

        composable<HomeMainDest> { entry ->
            val parentEntry = remember(entry) {
                navController.getBackStackEntry<HomeGraph>()
            }

            val viewModel: HomeViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

            HomeScreen(
                viewModel = viewModel,
                onViewFoodsList = { navController.navigate(FoodSelection) },
                onViewFoodLogDetails = { navController.navigate(FoodLogDetails) },
                onNavigateToGoals = { navController.navigate(ProfileGoalsDest) }
            )
        }

        composable<FoodSelection> { entry ->
            val parentEntry = remember(entry) {
                navController.getBackStackEntry<HomeGraph>()
            }

            val viewModel: HomeViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

            FoodSelectionScreen(
                viewModel,
                onBackClick = navController::popBackStack,
                onNavigateToSelectedFood = { navController.navigate(FoodLog) },
                onNavigateToFoodRequest = { navController.navigate(FoodRequest) }
            )
        }

        composable<FoodRequest> { entry ->
            val parentEntry = remember(entry) {
                navController.getBackStackEntry<HomeGraph>()
            }

            val viewModel: HomeViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

            FoodRequestScreen(
                viewModel,
                onBackClick = navController::popBackStack
            )
        }

        composable<FoodLogDetails> { entry ->
            val parentEntry = remember(entry) {
                navController.getBackStackEntry<HomeGraph>()
            }

            val viewModel: HomeViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

            FoodLogDetailsScreen(
                viewModel = viewModel,
                onBackClick = navController::popBackStack
            )
        }

        composable<FoodLog> { entry ->
            val parentEntry = remember(entry) {
                navController.getBackStackEntry<HomeGraph>()
            }

            val viewModel: HomeViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

            FoodLogScreen(
                onBackClick = navController::popBackStack,
                viewModel
            )
        }
    }
}