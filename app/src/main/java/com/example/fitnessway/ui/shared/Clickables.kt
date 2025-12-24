package com.example.fitnessway.ui.shared

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.fitnessway.util.Animation

object Clickables {
    enum class AppIconButtonSize(
        val size: Dp
    ) {
        LARGE(38.dp),
        MEDIUM(34.dp),
        SMALL(30.dp)
    }

    sealed interface AppIconButtonSource {
        data class Resource(@get:DrawableRes val id: Int) : AppIconButtonSource
        data class Vector(val imageVector: ImageVector) : AppIconButtonSource
    }

    @Composable
    fun AppIconButton(
        size: AppIconButtonSize = AppIconButtonSize.LARGE,
        showsClickIndication: Boolean = true,
        enabled: Boolean = true,
        icon: AppIconButtonSource,
        contentDescription: String,
        onClick: () -> Unit
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(CircleShape)
                .size(size.size)
                .clickable(
                    interactionSource = if (showsClickIndication) null else {
                        remember { MutableInteractionSource() }
                    },
                    indication = if (showsClickIndication) LocalIndication.current else null,
                    onClick = onClick,
                    enabled = enabled
                )
        ) {
            val tint by animateColorAsState(
                targetValue = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else {
                    MaterialTheme.colorScheme.surfaceVariant
                },
                animationSpec = Animation.colorSpec,
                label = "AppIconVectorButtonColorAnimation_$contentDescription"
            )

            val padding = (size.size.value / 6).dp
            val iconModifier = Modifier
                .fillMaxSize()
                .padding(padding)

            when (icon) {
                is AppIconButtonSource.Resource -> {
                    Icon(
                        painter = painterResource(icon.id),
                        contentDescription = contentDescription,
                        tint = tint,
                        modifier = iconModifier
                    )
                }

                is AppIconButtonSource.Vector -> {
                    Icon(
                        imageVector = icon.imageVector,
                        contentDescription = contentDescription,
                        tint = tint,
                        modifier = iconModifier
                    )
                }
            }
        }
    }
}