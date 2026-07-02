package com.example.fitnessway.ui.shared

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.fitnessway.ui.shared.Structure.AppIconSource
import com.example.fitnessway.ui.theme.tappable
import com.example.fitnessway.util.Ui

object Clickables {
    enum class AppIconButtonSize(
        val size: Dp
    ) {
        LARGE(38.dp),
        MEDIUM(34.dp),
        SMALL(28.dp),
        XS(24.dp)
    }

    /**
     * - Default `iconTint` is `MaterialTheme.colorScheme.onSurfaceVariant`
     * - When disabled `iconTint`'s value is `MaterialTheme.colorScheme.surfaceVariant`
     */
    @Composable
    fun AppPngIconButton(
        size: AppIconButtonSize = AppIconButtonSize.LARGE,
        showsClickIndication: Boolean = true,
        enabled: Boolean = true,
        icon: AppIconSource,
        iconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        iconTintOverridesDisabledTint: Boolean = false,
        contentDescription: String,
        modifier: Modifier = Modifier,
        onClick: () -> Unit,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .clip(CircleShape)
                .size(size.size)
                .tappable(
                    enabled = enabled,
                    showsTap = showsClickIndication,
                    onClick = onClick
                )
        ) {
            val tint by animateColorAsState(
                targetValue = if (iconTintOverridesDisabledTint) iconTint else {
                    if (enabled) iconTint else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                }
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
    fun DoneButton(
        enabled: Boolean,
        isLoading: Boolean = false,
        onClick: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier.size(Clickables.AppIconButtonSize.LARGE.size)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(Ui.Measurements.LOADING_CIRCLE_IN_HEADER_SIZE),
                    strokeWidth = Ui.Measurements.LOADING_CIRCLE_IN_HEADER_STROKE_WIDTH,
                )
            } else AppPngIconButton(
                icon = AppIconSource.Vector(AppVectors.checkmark),
                contentDescription = "Done",
                onClick = onClick,
                enabled = enabled,
                iconTint = MaterialTheme.colorScheme.primary
            )
        }
    }
}