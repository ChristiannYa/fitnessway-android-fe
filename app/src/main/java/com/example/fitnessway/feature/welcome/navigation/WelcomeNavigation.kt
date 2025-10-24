package com.example.fitnessway.feature.welcome.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.fitnessway.feature.welcome.screen.WelcomeMainScreen
import com.example.fitnessway.feature.welcome.screen.login.LoginScreen
import com.example.fitnessway.feature.welcome.screen.register.RegisterScreen
import com.example.navigation.WelcomeGraph
import kotlinx.serialization.Serializable

@Serializable
object WelcomeMain

@Serializable
private object Login

@Serializable
private object Register

fun NavGraphBuilder.welcomeNavigationGraph(navController: NavController) {
   navigation<WelcomeGraph>(WelcomeMain) {
      composable<WelcomeMain> {
         WelcomeMainScreen(
            onLoginClick = { navController.navigate(Login) },
            onRegisterClick = { navController.navigate(Register) }
         )
      }

      composable<Login> {
         LoginScreen(onBackClick = { navController.popBackStack() })
      }

      composable<Register> {
         RegisterScreen(onBackClick = { navController.popBackStack() })
      }
   }
}