package com.example.fitnessway.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fitnessway.data.state.IApplicationStateStore
import com.example.fitnessway.feature.home.navigation.HomeMainDest
import com.example.fitnessway.feature.home.navigation.homeNavigationGraph
import com.example.fitnessway.feature.lists.navigation.ListsMainDest
import com.example.fitnessway.feature.lists.navigation.listsNavigationGraph
import com.example.fitnessway.feature.profile.navigation.ProfileMainDest
import com.example.fitnessway.feature.profile.navigation.profileNavigationGraph
import com.example.fitnessway.feature.welcome.navigation.welcomeNavigationGraph
import com.example.fitnessway.ui.SplashScreen
import com.example.fitnessway.util.Animation
import org.koin.compose.koinInject

private val screenWithBottomNavBar = listOf(
    HomeMainDest::class,
    ListsMainDest::class,
    ProfileMainDest::class,
)

@Composable
fun AppNavigation(appStateStore: IApplicationStateStore = koinInject()) {
    val tokensState by appStateStore.authStateHolder.tokensState.collectAsState()
    val userState by appStateStore.userStateHolder.userState.collectAsState()

    val isUserLoading = tokensState.isAuthenticated && userState.user == null

    if (tokensState.isLoading || isUserLoading) {
        SplashScreen()
        return
    }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val shouldShowBottomBar = screenWithBottomNavBar.any { route ->
        currentDestination?.hasRoute(route) == true
    }

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar) {
                BottomNavigationBar(navController, currentDestination)
            }
        },
        content = { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = if (tokensState.isAuthenticated) HomeGraph else WelcomeGraph,
                exitTransition = {
                    if (isTabNavigation()) {
                        Animation.fadeOut
                    } else {
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            tween(300)
                        )
                    }
                },
                popEnterTransition = {
                    if (isTabNavigation()) {
                        Animation.fadeIn
                    } else {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            tween(300)
                        )
                    }
                },
                enterTransition = {
                    if (isTabNavigation()) {
                        Animation.fadeIn
                    } else {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            tween(300)
                        )
                    }
                },
                popExitTransition = {
                    if (isTabNavigation()) {
                        Animation.fadeOut
                    } else {
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            tween(300)
                        )
                    }
                },
                modifier = Modifier.padding(innerPadding)
            ) {
                welcomeNavigationGraph(navController)
                homeNavigationGraph(navController)
                listsNavigationGraph(navController)
                profileNavigationGraph(navController)
            }
        }
    )
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.isTabNavigation(): Boolean {
    return screenWithBottomNavBar.any { route ->
        targetState.destination.hasRoute(route)
    } && screenWithBottomNavBar.any { route ->
        initialState.destination.hasRoute(route)
    }
}