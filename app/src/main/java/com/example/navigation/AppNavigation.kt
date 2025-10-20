package com.example.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fitnessway.feature.home.navigation.HomeMain
import com.example.fitnessway.feature.home.navigation.homeNavigationGraph
import com.example.fitnessway.feature.profile.navigation.ProfileMain
import com.example.fitnessway.feature.profile.navigation.profileNavigationGraph
import com.example.fitnessway.feature.welcome.navigation.welcomeNavigationGraph

private val screenWithBottomNavBar = listOf(
   HomeMain::class,
   ProfileMain::class,
)

@Composable
fun AppNavigation() {
   val navController = rememberNavController()
   val navBackStackEntry by navController.currentBackStackEntryAsState()
   val currentDestination = navBackStackEntry?.destination

   val shouldShowBottomBar = screenWithBottomNavBar.any { route ->
      currentDestination?.hasRoute(route) == true
   }

   Scaffold(
      bottomBar = {
         if (shouldShowBottomBar) {
            BottomNavigationBar(
               navController,
               currentDestination
            )
         }
      }
   ) { innerPadding ->
      NavHost(
         navController = navController,
         startDestination = WelcomeGraph,
         modifier = Modifier.padding(innerPadding)
      ) {
         welcomeNavigationGraph(navController)
         homeNavigationGraph(navController)
         profileNavigationGraph(navController)
      }
   }
}