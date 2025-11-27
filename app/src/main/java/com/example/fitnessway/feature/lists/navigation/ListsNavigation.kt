package com.example.fitnessway.feature.lists.navigation

import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.fitnessway.feature.lists.screen.details.DetailsScreen
import com.example.fitnessway.feature.lists.screen.main.ListsScreen
import com.example.fitnessway.feature.lists.viewmodel.ListsViewModel
import com.example.fitnessway.navigation.ListsGraph
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
object ListsMain

@Serializable
object Details

fun NavGraphBuilder.listsNavigationGraph(navController: NavController) {
    navigation<ListsGraph>(startDestination = ListsMain) {
        // @NOTE: parentEntry's role is so that the same instance of the viewmodel
        // is persisted across screen navigation

        composable<ListsMain> { entry ->
            val parentEntry = remember(entry) {
                navController.getBackStackEntry<ListsGraph>()
            }

            val viewModel: ListsViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

            ListsScreen(
                viewModel = viewModel,
                onViewDetails = { navController.navigate(Details) }
            )
        }

        composable<Details> { entry ->
            val parentEntry = remember(entry) {
                navController.getBackStackEntry<ListsGraph>()
            }

            val viewModel: ListsViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

            DetailsScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}