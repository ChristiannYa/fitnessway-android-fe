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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.fitnessway.ui.theme.AppModifiers.areaContainer
import com.example.fitnessway.util.Ui

object Loading {
    @Composable
    fun Area(
        text: String? = null,
        modifier: Modifier = Modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier.fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (text != null) {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                CircularProgressIndicator(
                    strokeWidth = Ui.Measurements.LOADING_CIRCLE_IN_SCREEN_STROKE_WIDTH,
                    modifier = Modifier.size(Ui.Measurements.LOADING_CIRCLE_IN_SCREEN_SIZE)
                )
            }
        }
    }

    @Composable
    fun Composable(
        height: Dp,
        text: String? = null,
        areaColor: Color = MaterialTheme.colorScheme.surfaceVariant,
        modifier: Modifier = Modifier
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "pulse_loading")
        val alpha by infiniteTransition.animateFloat(
            initialValue = 0.1f,
            targetValue = 0.3f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 600,
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "alpha_loading"
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .alpha(alpha)
                .areaContainer(
                    areaColor = areaColor
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

    @Composable
    fun RefreshByPullIndicator(
        isRefreshing: Boolean,
        state: PullToRefreshState,
        containerColor: Color = MaterialTheme.colorScheme.surfaceTint,
        color: Color = MaterialTheme.colorScheme.primary,
        modifier: Modifier = Modifier
    ) {
        Indicator(
            isRefreshing = isRefreshing,
            state = state,
            containerColor = containerColor,
            color = color,
            modifier = modifier
        )
    }

    @Composable
    fun SpinnerInScreen() {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            CircularProgressIndicator(
                strokeWidth = Ui.Measurements.LOADING_CIRCLE_IN_SCREEN_STROKE_WIDTH,
                modifier = Modifier
                    .size(
                        Ui.Measurements.LOADING_CIRCLE_IN_SCREEN_SIZE
                    )
            )
        }
    }
}