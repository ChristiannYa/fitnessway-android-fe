package com.example.fitnessway.util

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object Animation {
    @Composable
    fun rememberHeaderSlideUpAnimation(shouldSlideUp: Boolean): Pair<Dp, Modifier> {
        var headerHeight by remember { mutableIntStateOf(0) }

        val headerOffset by animateDpAsState(
            targetValue = if (shouldSlideUp) {
                with(LocalDensity.current) { -headerHeight.toDp() }
            } else 0.dp,
            animationSpec = tween(durationMillis = 300),
            label = "headerOffset"
        )

        val headerModifier = Modifier
            .onSizeChanged { size -> headerHeight = size.height }
            .offset(y = headerOffset)

        return headerOffset to headerModifier
    }

    val colorSpec: AnimationSpec<Color> = tween(durationMillis = 300)

    object ComposableTransition {
        val fadeIn = fadeIn(animationSpec = tween(300))
        val fadeOut = fadeOut(animationSpec = tween(300))

        object PopUpV1 {
            val enter = fadeIn + scaleIn(
                initialScale = 0.2f,
                transformOrigin = TransformOrigin(1f, 0f),
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )

            val exit = fadeOut + scaleOut(
                targetScale = 0.1f,
                transformOrigin = TransformOrigin(1f, 0f),
                animationSpec = tween(durationMillis = 150)
            )
        }

        object PopUpV2 {
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

            val exit = slideOutVertically() +
                    shrinkVertically() +
                    fadeOut() +
                    scaleOut(targetScale = 1.2f)
        }

        object PopUpV3 {
            val enter = slideInVertically(
                initialOffsetY = { it }, // Start from bottom (full height offset)
                animationSpec = tween(durationMillis = 300)
            ) + fadeIn

            val exit = slideOutVertically(
                targetOffsetY = { -it }, // Exit to bottom
                animationSpec = tween(durationMillis = 300)
            ) + fadeOut
        }

        object SlideVerticallyFromBottom {
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