package com.example.fitnessway.ui.shared

import android.view.SoundEffectConstants
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp

@Composable
fun Header(
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null,
    title: String? = null,
    isOnBackEnabled: Boolean? = true,
    extraContent: (@Composable () -> Unit)? = null,
) {
    val view = LocalView.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = 16.dp + if (onBackClick != null) 0.dp else 8.dp,
                end = 16.dp,
                bottom = 2.dp
            )
    ) {
        val enabled = isOnBackEnabled == true

        // Back button with title
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (onBackClick != null) {
                val iconTint by animateColorAsState(
                    targetValue = if (enabled) {
                        MaterialTheme.colorScheme.onBackground
                    } else MaterialTheme.colorScheme.surfaceVariant
                )

                Clickables.AppPngIconButton(
                    icon = Structure.AppIconSource.Vector(Icons.AutoMirrored.Filled.ArrowBack),
                    enabled = enabled,
                    contentDescription = "Go back",
                    onClick = {
                        view.playSoundEffect(SoundEffectConstants.NAVIGATION_LEFT)
                        onBackClick()
                    },
                    iconTint = iconTint
                )
            }

            if (title != null) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }

        // Extra content
        if (extraContent != null) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) { extraContent() }
        }
    }
}