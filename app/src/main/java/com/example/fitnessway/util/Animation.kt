package com.example.fitnessway.util

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
}