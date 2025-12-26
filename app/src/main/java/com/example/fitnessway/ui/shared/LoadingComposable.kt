package com.example.fitnessway.ui.shared

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.Dp
import com.example.fitnessway.ui.theme.AppModifiers.areaContainer

@Composable
fun LoadingComposable(
    height: Dp,
    text: String? = null
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_loading")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 800,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha_loading"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .alpha(alpha)
            .areaContainer(
                areaColor = MaterialTheme.colorScheme.surfaceVariant
            )
            .height(height)
    ) {
        if (text != null) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}