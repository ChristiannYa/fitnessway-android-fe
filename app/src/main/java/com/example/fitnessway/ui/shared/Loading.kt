package com.example.fitnessway.ui.shared

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.fitnessway.ui.theme.AppModifiers.areaContainer
import com.example.fitnessway.util.Ui

object Loading {
    @Composable
    fun LoadingArea(
        text: String? = null
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (text != null) {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                CircularProgressIndicator(
                    strokeWidth = Ui.Measurements.PROGRESS_INDICATOR_STROKE_WIDTH,
                    modifier = Modifier.size(Ui.Measurements.PROGRESS_INDICATOR_SIZE)
                )
            }
        }
    }

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
}