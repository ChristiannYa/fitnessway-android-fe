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
    fun Modifier.areaContainer() = this
        .fillMaxWidth()
        .background(
            color = MaterialTheme.colorScheme.inverseSurface.copy(0.01f),
            shape = RoundedCornerShape(21.dp)
        )
        .padding(24.dp)

    /*
    @Composable
    fun Modifier.smallCard() = this
        .background(
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(12.dp)
        )
        .padding(12.dp)

     */
}