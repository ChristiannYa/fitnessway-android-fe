package com.example.fitnessway.ui.shared

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.TransformOrigin

@Composable
fun ApiErrorMessageAnimated(
    isVisible: Boolean,
    errorMessage: String
) {
    AnimatedVisibility(
        visible = isVisible,
        enter =
            slideInVertically(
                // Start the slide from 40 (pixels) above where the content is supposed to go, to
                // produce a parallax effect
                initialOffsetY = { -40 }
            ) +
                    expandVertically(expandFrom = Alignment.Top) +
                    scaleIn(
                        // Animate scale from 0f to 1f using the top center as the pivot point.
                        transformOrigin = TransformOrigin(
                            pivotFractionX = .5f,
                            pivotFractionY = 0f
                        )
                    ) +
                    fadeIn(initialAlpha = 0.3f),
        exit = slideOutVertically() +
                shrinkVertically() +
                fadeOut() +
                scaleOut(targetScale = 1.2f),
        content = {
            ApiErrorMessage(errorMessage)
        }
    )
}