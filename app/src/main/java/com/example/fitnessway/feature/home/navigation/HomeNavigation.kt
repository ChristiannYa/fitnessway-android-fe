package com.example.fitnessway.feature.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.fitnessway.feature.home.screen.foodselection.FoodSelectionScreen
import com.example.fitnessway.feature.home.screen.main.HomeScreen
import com.example.fitnessway.navigation.HomeGraph
import kotlinx.serialization.Serializable

@Serializable
object HomeMain

@Serializable
private object FoodSelection

fun NavGraphBuilder.homeNavigationGraph(navController: NavController) {
   navigation<HomeGraph>(startDestination = HomeMain) {
      composable<HomeMain> {
         HomeScreen(
            onFoodSelection = {
               navController.navigate(FoodSelection)
            }
         )
      }

      composable<FoodSelection> {
         FoodSelectionScreen(
            onBackClick = {
               navController.popBackStack()
            }
         )
      }
   }
}