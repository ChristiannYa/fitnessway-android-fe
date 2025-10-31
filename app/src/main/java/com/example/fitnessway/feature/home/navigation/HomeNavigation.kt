package com.example.fitnessway.feature.home.navigation

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.fitnessway.feature.home.screen.foodselection.FoodSelectionScreen
import com.example.fitnessway.feature.home.screen.main.HomeScreen
import com.example.fitnessway.feature.home.viewmodel.HomeViewModel
import com.example.fitnessway.navigation.HomeGraph
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
object HomeMain

@Serializable
private object FoodSelection

fun NavGraphBuilder.homeNavigationGraph(navController: NavController) {
    navigation<HomeGraph>(startDestination = HomeMain) {
        composable<HomeMain> { entry ->
            val parentEntry = remember(entry) {
                navController.getBackStackEntry<HomeGraph>()
            }

            val viewModel: HomeViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

            HomeScreen(
                onFoodLog = { navController.navigate(FoodSelection) },
                viewModel
            )
        }

        composable<FoodSelection> { entry ->
            val parentEntry = remember(entry) {
                navController.getBackStackEntry<HomeGraph>()
            }

            val viewModel: HomeViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

            FoodSelectionScreen(
                onBackClick = { navController.popBackStack() },
                viewModel
            )
        }
    }
}