package com.example.fitnessway.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination

private data class TopLevelRoute<T : Any>(
    val name: String,
    val route: T,
    val icon: ImageVector
)

private val topLevelRoutes = listOf(
    TopLevelRoute(
        name = "Home",
        route = HomeGraph,
        icon = Icons.Default.Home
    ),
    TopLevelRoute(
        name = "Lists",
        route = ListsGraph,
        icon = Icons.Default.ClearAll
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
    NavigationBar(
        containerColor = Color.Transparent,
        content = {
            topLevelRoutes.forEach { topLevelRoute ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = topLevelRoute.icon,
                            contentDescription = topLevelRoute.name
                        )
                    },
                    label = {
                        Text(
                            text = topLevelRoute.name,
                            fontFamily = FontFamily.Serif
                        )
                    },
                    colors = NavigationBarItemColors(
                        selectedIconColor = MaterialTheme.colorScheme.background,
                        selectedTextColor = MaterialTheme.colorScheme.inverseSurface,
                        selectedIndicatorColor = MaterialTheme.colorScheme.inverseSurface,
                        unselectedIconColor = MaterialTheme.colorScheme.inverseSurface,
                        unselectedTextColor = MaterialTheme.colorScheme.inverseSurface,
                        disabledIconColor = MaterialTheme.colorScheme.inverseSurface.copy(0.5f),
                        disabledTextColor = MaterialTheme.colorScheme.inverseSurface.copy(0.5f)
                    ),
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
    )
}