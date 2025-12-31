package com.example.fitnessway.ui.shared

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.fitnessway.util.Animation

@Composable
fun DarkOverlay(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = Animation.ComposableTransition.fadeIn,
        exit = Animation.ComposableTransition.fadeOut,
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.8f))
                .clickable(
                    onClick = { onClick?.invoke() },
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    enabled = enabled
                )
        )
    }
}