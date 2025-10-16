package com.example.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.fitnessway.feature.welcome.navigation.welcomeNavigationGraph

@Composable
fun AppNavigation() {
   val navController = rememberNavController()
//   val navBackStackEntry by navController.currentBackStackEntryAsState()
//   val currentDestination = navBackStackEntry?.destination

   Scaffold { innerPadding ->
      NavHost (
         navController = navController,
         startDestination = WelcomeGraph,
         modifier = Modifier
            .padding(innerPadding)
      ) {
         welcomeNavigationGraph(navController)
      }
   }
}