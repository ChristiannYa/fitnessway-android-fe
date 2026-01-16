package com.example.fitnessway.feature.lists.navigation

import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.fitnessway.feature.lists.screen.create.food.CreateFoodFormScreen
import com.example.fitnessway.feature.lists.screen.details.FoodDetailsScreen
import com.example.fitnessway.feature.lists.screen.details.edition.FoodEditionScreen
import com.example.fitnessway.feature.lists.screen.main.ListsScreen
import com.example.fitnessway.feature.lists.viewmodel.ListsViewModel
import com.example.fitnessway.navigation.ListsGraph
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
object ListsMainDest

@Serializable
private object DetailsDest

@Serializable
private object FoodCreationDest

@Serializable
private object FoodEditionDest

fun NavGraphBuilder.listsNavigationGraph(navController: NavController) {
    navigation<ListsGraph>(startDestination = ListsMainDest) {

        composable<ListsMainDest> { entry ->
            val parentEntry = remember(entry) {
                navController.getBackStackEntry<ListsGraph>()
            }

            val viewModel: ListsViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

            ListsScreen(
                viewModel = viewModel,
                onViewFoodDetails = { navController.navigate(DetailsDest) },
                onNavigateToFoodCreationForm = { navController.navigate(FoodCreationDest) }
            )
        }

        composable<FoodCreationDest> { entry ->
            val parentEntry = remember(entry) {
                navController.getBackStackEntry<ListsGraph>()
            }

            val viewModel: ListsViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

            CreateFoodFormScreen(
                viewModel = viewModel,
                onBackClick = navController::popBackStack
            )
        }

        composable<DetailsDest> { entry ->
            val parentEntry = remember(entry) {
                navController.getBackStackEntry<ListsGraph>()
            }

            val viewModel: ListsViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

            FoodDetailsScreen(
                viewModel = viewModel,
                onBackClick = navController::popBackStack,
                onNavigateToEditionScreen = { navController.navigate(FoodEditionDest) }
            )
        }

        composable<FoodEditionDest> { entry ->
            val parentEntry = remember(entry) {
                navController.getBackStackEntry<ListsGraph>()
            }

            val viewModel: ListsViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

            FoodEditionScreen(
                viewModel = viewModel,
                onBackClick = navController::popBackStack
            )
        }
    }
}