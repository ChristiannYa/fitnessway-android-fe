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
            shape = RoundedCornerShape(16.dp)
        )
        .padding(18.dp)

    @Composable
    fun Modifier.areaContainerMedium() = this
        .fillMaxWidth()
        .background(
            color = MaterialTheme.colorScheme.inverseSurface.copy(0.03f),
            shape = RoundedCornerShape(14.dp)
        )
        .padding(20.dp)

    @Composable
    fun Modifier.areaContainerSmall() = this
        .fillMaxWidth()
        .background(
            color = MaterialTheme.colorScheme.inverseSurface.copy(0.03f),
            shape = RoundedCornerShape(12.dp)
        )
        .padding(14.dp)
}