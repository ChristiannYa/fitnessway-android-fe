package com.example.fitnessway.feature.lists.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.fitnessway.feature.lists.screen.main.ListsScreen
import com.example.fitnessway.navigation.ListsGraph
import kotlinx.serialization.Serializable

@Serializable
object ListsMain

fun NavGraphBuilder.listsNavigationGraph(navController: NavController) {
    navigation<ListsGraph>(startDestination = ListsMain) {
        composable<ListsMain> {
            ListsScreen()
        }
    }
}