package com.example.fitnessway.feature.home.navigation

import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.fitnessway.feature.home.screen.create.food.CreateFoodFormScreen
import com.example.fitnessway.feature.home.screen.foodselection.FoodSelectionScreen
import com.example.fitnessway.feature.home.screen.foodselection.foodlog.FoodLogScreen
import com.example.fitnessway.feature.home.screen.logdetails.LogDetailsScreen
import com.example.fitnessway.feature.home.screen.main.HomeScreen
import com.example.fitnessway.feature.home.viewmodel.HomeViewModel
import com.example.fitnessway.feature.profile.navigation.ProfileGoalsDest
import com.example.fitnessway.navigation.HomeGraph
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
object HomeMain

@Serializable
private object FoodSelection

@Serializable
private object FoodCreation

@Serializable
private object FoodLogDetails

@Serializable
private object FoodLog

fun NavGraphBuilder.homeNavigationGraph(navController: NavController) {
    navigation<HomeGraph>(startDestination = HomeMain) {
        composable<HomeMain> { entry ->
            val parentEntry = remember(entry) {
                navController.getBackStackEntry<HomeGraph>()
            }

            val viewModel: HomeViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

            HomeScreen(
                viewModel = viewModel,
                onViewFoodsList = { navController.navigate(FoodSelection) },
                onViewFoodLogDetails = { navController.navigate(FoodLogDetails) },
                onNavigateToFoodForm = { navController.navigate(FoodCreation) },
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
                onBackClick = { navController.popBackStack() },
                onSelectedFoodToLog = { navController.navigate(FoodLog) }
            )
        }

        composable<FoodCreation> { entry ->
            val parentEntry = remember(entry) {
                navController.getBackStackEntry<HomeGraph>()
            }

            val viewModel: HomeViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

            CreateFoodFormScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<FoodLogDetails> { entry ->
            val parentEntry = remember(entry) {
                navController.getBackStackEntry<HomeGraph>()
            }

            val viewModel: HomeViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

            LogDetailsScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<FoodLog> { entry ->
            val parentEntry = remember(entry) {
                navController.getBackStackEntry<HomeGraph>()
            }

            val viewModel: HomeViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

            FoodLogScreen(
                onBackClick = { navController.popBackStack() },
                viewModel
            )
        }
    }
}