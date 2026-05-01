package com.example.fitnessway.util

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin

object Animation {
    val colorSpec: AnimationSpec<Color> = tween(durationMillis = 300)

    object ComposableTransition {
        val fadeIn = fadeIn(animationSpec = tween(300))
        val fadeOut = fadeOut(animationSpec = tween(300))

        object ScaleInWithSpring {

            fun enter(transformOrigin: PopupOrigin) = fadeIn + scaleIn(
                initialScale = 0.2f,
                transformOrigin = transformOrigin.origin,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )

            fun exit(transformOrigin: PopupOrigin) = fadeOut + scaleOut(
                targetScale = 0.1f,
                transformOrigin = transformOrigin.origin,
                animationSpec = tween(durationMillis = 150)
            )
        }

        object VerticalSlideExtra {
            val enter = slideInVertically(
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
                    fadeIn(initialAlpha = 0.3f)

            val exit = slideOutVertically(
                targetOffsetY = { -40 }
            ) +
                    shrinkVertically(shrinkTowards = Alignment.Top) +
                    scaleOut(
                        transformOrigin = TransformOrigin(
                            pivotFractionX = .5f,
                            pivotFractionY = 0f
                        )
                    ) +
                    fadeOut(targetAlpha = 0.3f)
        }

        object VerticalSlideFromBottom {
            val enter = slideInVertically(
                initialOffsetY = { it }, // Start from bottom (full height offset)
                animationSpec = tween(durationMillis = 300)
            ) + fadeIn

            val exit = slideOutVertically(
                targetOffsetY = { -it }, // Exit to bottom
                animationSpec = tween(durationMillis = 300)
            ) + fadeOut
        }

        object VerticalSlideFromTop {
            val enter = slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(durationMillis = 300)
            )

            val exit = slideOutVertically(
                targetOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(durationMillis = 300)
            )
        }
    }
}