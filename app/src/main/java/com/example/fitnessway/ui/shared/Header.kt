package com.example.fitnessway.ui.shared

import android.view.SoundEffectConstants
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
                start = if (onBackClick != null) 0.dp else 16.dp,
                end = 16.dp,
                bottom = 2.dp
            )
    ) {
        val enabled = isOnBackEnabled == true

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (onBackClick != null) {
                IconButton(
                    onClick = {
                        view.playSoundEffect(SoundEffectConstants.NAVIGATION_LEFT)
                        onBackClick()
                    },
                    enabled = enabled
                ) {
                    val iconTint by animateColorAsState(
                        targetValue = if (enabled) {
                            MaterialTheme.colorScheme.onBackground
                        } else MaterialTheme.colorScheme.surfaceVariant
                    )

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go Back",
                        tint = iconTint
                    )
                }
            }

            if (title != null) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }

        if (extraContent != null) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) { extraContent() }
        }
    }
}