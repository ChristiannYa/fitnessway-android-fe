package com.example.fitnessway.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.zIndex
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fitnessway.data.state.IApplicationStateStore
import com.example.fitnessway.feature.home.navigation.HomeMain
import com.example.fitnessway.feature.home.navigation.homeNavigationGraph
import com.example.fitnessway.feature.lists.navigation.ListsMain
import com.example.fitnessway.feature.lists.navigation.listsNavigationGraph
import com.example.fitnessway.feature.profile.navigation.ProfileMain
import com.example.fitnessway.feature.profile.navigation.profileNavigationGraph
import com.example.fitnessway.feature.welcome.navigation.welcomeNavigationGraph
import com.example.fitnessway.ui.SplashScreen
import org.koin.compose.koinInject

private val screenWithBottomNavBar = listOf(
    HomeMain::class,
    ListsMain::class,
    ProfileMain::class,
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

    val bottomBarHeight = 300f

    Scaffold(
        bottomBar = {
            val animatedTranslation by animateFloatAsState(
                targetValue = if (shouldShowBottomBar) 0f else bottomBarHeight,
                animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
                label = "bottomBarTranslation"
            )

            val animatedAlpha by animateFloatAsState(
                targetValue = if (shouldShowBottomBar) 1f else 0f,
                animationSpec = tween(durationMillis = 400, easing = LinearEasing),
                label = "bottomBarAlpha"
            )

            // Always render it to preserve layout space
            Box(
                modifier = Modifier
                    .zIndex(if (shouldShowBottomBar) 1f else -1f) // Send behind when hidden
                    .graphicsLayer {
                        translationY = animatedTranslation
                        alpha = animatedAlpha
                    },
                content = {
                    BottomNavigationBar(navController, currentDestination)
                }
            )
        },
        content = { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = if (tokensState.isAuthenticated) HomeGraph else WelcomeGraph,
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