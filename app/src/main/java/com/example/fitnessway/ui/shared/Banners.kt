package com.example.fitnessway.ui.shared

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.fitnessway.util.Animation
import com.example.fitnessway.util.Ui

object Banners {
    @Composable
    fun AppBanner(
        text: String,
        borderColor: Color,
        imageVector: ImageVector,
        iconTint: Color,
        modifier: Modifier = Modifier
    ) {
        val shape = RoundedCornerShape(8.dp)

        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = shape,
            shadowElevation = 4.dp,
            modifier = modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = borderColor,
                    shape = shape
                )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Ui.Measurements.TEXT_ICON_HORIZONTAL_SPACE),
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = imageVector,
                    contentDescription = null,
                    tint = iconTint
                )

                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }

    @Composable
    fun ErrorBanner(
        text: String,
        modifier: Modifier = Modifier
    ) {
        AppBanner(
            text = text,
            borderColor = MaterialTheme.colorScheme.error,
            imageVector = Icons.Default.Warning,
            iconTint = MaterialTheme.colorScheme.error,
            modifier = modifier
        )
    }

    @Composable
    fun ErrorBannerAnimated(
        isVisible: Boolean,
        text: String
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = Animation.ComposableTransition.PopUpV2.enter,
            exit = Animation.ComposableTransition.PopUpV2.exit
        ) { ErrorBanner(text) }
    }

    @Composable
    fun SuccessBanner(
        text: String,
        modifier: Modifier = Modifier
    ) {
        AppBanner(
            text = text,
            borderColor = MaterialTheme.colorScheme.surfaceVariant,
            imageVector = Icons.Default.CheckCircle,
            iconTint = MaterialTheme.colorScheme.primary,
            modifier = modifier
        )
    }

    @Composable
    fun SuccessBannerAnimated(
        text: String,
        isVisible: Boolean,
        modifier: Modifier = Modifier
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = Animation.ComposableTransition.FadeInV1.enter,
            exit = Animation.ComposableTransition.FadeInV1.exit,
            modifier = modifier
        ) { SuccessBanner(text) }
    }
}