package com.example.fitnessway.ui.shared

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.fitnessway.ui.shared.Structure.AppIconButtonSource
import com.example.fitnessway.util.Animation
import com.example.fitnessway.util.Ui

object Clickables {
    enum class AppIconButtonSize(
        val size: Dp
    ) {
        LARGE(38.dp),
        MEDIUM(34.dp),
        SMALL(28.dp)
    }

    @Composable
    fun AppIconButton(
        size: AppIconButtonSize = AppIconButtonSize.LARGE,
        showsClickIndication: Boolean = true,
        enabled: Boolean = true,
        icon: AppIconButtonSource,
        iconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                targetValue = if (enabled) iconTint else {
                    MaterialTheme.colorScheme.surfaceVariant
                },
                animationSpec = Animation.colorSpec,
                label = "AppIconVectorButtonColorAnimation_$contentDescription"
            )

            val padding = (size.size.value / 6).dp

            Structure.AppIconDynamic(
                source = icon,
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        }
    }

    @Composable
    fun HeaderDoneButton(
        onClick: () -> Unit,
        enabled: Boolean,
        isLoading: Boolean = false
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(Ui.Measurements.LOADING_CIRCLE_IN_HEADER_SIZE), // log the padding of AppIconButton to see its value
                strokeWidth = Ui.Measurements.LOADING_CIRCLE_IN_HEADER_STROKE_WIDTH,
            )
        } else {
            AppIconButton(
                icon = AppIconButtonSource.Vector(Icons.Default.Done),
                contentDescription = "Done",
                onClick = onClick,
                enabled = enabled,
                iconTint = MaterialTheme.colorScheme.primary
            )
        }
    }
}