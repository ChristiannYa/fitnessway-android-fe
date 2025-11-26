package com.example.fitnessway.ui.theme

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object AppModifiers {
    @Composable
    fun Modifier.areaContainerLarge(
        areaColor: Color = MaterialTheme.colorScheme.primaryContainer,
        shape: RoundedCornerShape = RoundedCornerShape(16.dp),
    ) = this
        .fillMaxWidth()
        .background(
            color = areaColor,
            shape = shape
        )
        .padding(18.dp)

    @Composable
    fun Modifier.areaContainerMedium(
        areaColor: Color = MaterialTheme.colorScheme.primaryContainer,
        shape: RoundedCornerShape = RoundedCornerShape(14.dp),
    ) = this
        .fillMaxWidth()
        .background(
            color = areaColor,
            shape = shape
        )
        .padding(16.dp)

    @Composable
    fun Modifier.areaContainerSmall(
        areaColor: Color = MaterialTheme.colorScheme.primaryContainer,
        shape: RoundedCornerShape = RoundedCornerShape(12.dp),
        showsIndication: Boolean = true,
        onClick: (() -> Unit)? = null,
    ) = this
        .then(
            if (onClick != null) {
                Modifier
                    .clip(shape)
                    .clickable(
                        interactionSource = if (showsIndication) null else {
                            remember { MutableInteractionSource() }
                        },
                        indication = if (showsIndication) LocalIndication.current else null,
                        onClick = onClick
                    )
            } else Modifier
        )
        .fillMaxWidth()
        .background(
            color = areaColor,
            shape = shape
        )
        .padding(14.dp)
}