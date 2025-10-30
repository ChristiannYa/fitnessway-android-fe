package com.example.fitnessway.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

object AppModifiers {
    @Composable
    fun Modifier.areaContainerLarge() = this
        .fillMaxWidth()
        .background(
            color = MaterialTheme.colorScheme.inverseSurface.copy(0.03f),
            shape = RoundedCornerShape(20.dp)
        )
        .padding(20.dp)

    @Composable
    fun Modifier.areaContainerMedium() = this
        .fillMaxWidth()
        .background(
            color = MaterialTheme.colorScheme.inverseSurface.copy(0.03f),
            shape = RoundedCornerShape(18.dp)
        )
        .padding(18.dp)
}