package com.example.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.fitnessway.data.model.navigation.TopLevelRoute

private val topLevelRoutes = listOf(
   TopLevelRoute(
      name = "Home",
      route = HomeGraph,
      icon = Icons.Default.Home
   ),
   TopLevelRoute(
      name = "Profile",
      route = ProfileGraph,
      icon = Icons.Default.Person
   ),
)

@Composable
fun BottomNavigationBar(
   navController: NavController,
   currentDestination: NavDestination?
) {
   NavigationBar {
      topLevelRoutes.forEach { topLevelRoute ->
         NavigationBarItem(
            icon = {
               Icon(
                  imageVector = topLevelRoute.icon,
                  contentDescription = topLevelRoute.name
               )
            },
            label = { Text(topLevelRoute.name) },
            selected = currentDestination?.hierarchy?.any {
               it.hasRoute(topLevelRoute.route::class)
            } == true,
            onClick = {
               navController.navigate(topLevelRoute.route) {
                  // Pop up to the start destination of the graph to avoid
                  // building up a large stack of destinations on the back
                  // stack as users select items
                  popUpTo(navController.graph.findStartDestination().id) {
                     saveState = true
                  }
                  // Avoid multiple copies of the same destination when reselecting
                  // the same item
                  launchSingleTop = true
                  // Restore state when reselecting a previously selected item
                  restoreState = true
               }
            }
         )
      }
   }
}