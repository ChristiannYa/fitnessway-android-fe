package com.example.fitnessway.feature.lists.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.fitnessway.feature.lists.screen.create.CreateFoodFormScreen
import com.example.fitnessway.feature.lists.screen.main.ListsScreen
import com.example.fitnessway.feature.lists.screen.pending_details.PendingFoodDetailsScreen
import com.example.fitnessway.feature.lists.screen.request.FoodRequestScreen
import com.example.fitnessway.feature.lists.screen.user_details.UserEdibleDetailsScreen
import com.example.fitnessway.feature.lists.screen.user_details.edition.FoodEditionScreen
import com.example.fitnessway.feature.lists.viewmodel.ListsViewModel
import com.example.fitnessway.navigation.ListsGraph
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
object ListsMainDest

@Serializable
private object UserFoodDetailsDest

@Serializable
private object PendingFoodDetailsDest

@Serializable
private object FoodRequestDest

@Serializable
private object FoodCreationDest

@Serializable
private object FoodEditionDest

fun NavGraphBuilder.listsNavigationGraph(navController: NavController) {
    navigation<ListsGraph>(startDestination = ListsMainDest) {

        @Composable
        fun getViewModel(entry: NavBackStackEntry): ListsViewModel {
            val parentEntry = remember(entry) {
                navController.getBackStackEntry<ListsGraph>()
            }

            return koinViewModel(viewModelStoreOwner = parentEntry)
        }

        composable<ListsMainDest> {
            ListsScreen(
                viewModel = getViewModel(it),
                onNavigateToUserEdibleDetails = { navController.navigate(UserFoodDetailsDest) },
                onNavigateToPendingEdibleDetails = { navController.navigate(PendingFoodDetailsDest) },
                onNavigateToEdibleRequestScreen = { navController.navigate(FoodRequestDest) },
                onNavigateToEdibleCreationForm = { navController.navigate(FoodCreationDest) }
            )
        }

        composable<FoodRequestDest> {
            FoodRequestScreen(
                viewModel = getViewModel(it),
                onBackClick = navController::popBackStack
            )
        }

        composable<FoodCreationDest> {
            CreateFoodFormScreen(
                viewModel = getViewModel(it),
                onBackClick = navController::popBackStack
            )
        }

        composable<UserFoodDetailsDest> {
            UserEdibleDetailsScreen(
                viewModel = getViewModel(it),
                onBackClick = navController::popBackStack,
                onNavigateToEditionScreen = { navController.navigate(FoodEditionDest) }
            )
        }

        composable<PendingFoodDetailsDest> {
            PendingFoodDetailsScreen(
                viewModel = getViewModel(it),
                onBackClick = navController::popBackStack
            )
        }

        composable<FoodEditionDest> {
            FoodEditionScreen(
                viewModel = getViewModel(it),
                onBackClick = navController::popBackStack
            )
        }
    }
}