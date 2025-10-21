package com.example.fitnessway.data.model.navigation

import androidx.compose.ui.graphics.vector.ImageVector

data class TopLevelRouteModel<T : Any>(
   val name: String,
   val route: T,
   val icon: ImageVector
)