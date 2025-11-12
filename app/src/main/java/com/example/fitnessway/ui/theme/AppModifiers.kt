package com.example.fitnessway.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object AppModifiers {
    @Composable
    fun Modifier.areaContainerLarge(
        areaColor: Color = MaterialTheme.colorScheme.primaryContainer
    ) = this
        .fillMaxWidth()
        .background(
            color = areaColor,
            shape = RoundedCornerShape(16.dp)
        )
        .padding(18.dp)

    @Composable
    fun Modifier.areaContainerMedium(
        areaColor: Color = MaterialTheme.colorScheme.primaryContainer
    ) = this
        .fillMaxWidth()
        .background(
            color = areaColor,
            shape = RoundedCornerShape(14.dp)
        )
        .padding(20.dp)

    @Composable
    fun Modifier.areaContainerSmall(
        areaColor: Color = MaterialTheme.colorScheme.primaryContainer,
        onClick: (() -> Unit)? = null
    ) = this
        .then(
            if (onClick != null) {
                Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onClick)
            } else Modifier
        )
        .fillMaxWidth()
        .background(
            color = areaColor,
            shape = RoundedCornerShape(12.dp)
        )
        .padding(14.dp)
}