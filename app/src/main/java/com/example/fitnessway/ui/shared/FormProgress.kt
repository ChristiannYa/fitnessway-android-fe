package com.example.fitnessway.ui.shared

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.example.fitnessway.util.Animation

@Composable
fun FormProgress(
    currentStep: Int,
    stepValidations: List<Boolean>,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterHorizontally),
        modifier = modifier
    ) {
        stepValidations.forEachIndexed { index, isValid ->
            ProgressIndicator(
                isCurrentStep = currentStep == index + 1,
                isValid = isValid
            )
        }
    }
}

@Composable
private fun ProgressIndicator(
    isCurrentStep: Boolean,
    isValid: Boolean,
) {
    val scale by animateFloatAsState(
        targetValue = if (isCurrentStep) 1f else 0.6f,
        animationSpec = tween(durationMillis = 300),
        label = "food_form_SCALE_ANIMATION"
    )

    val color by animateColorAsState(
        targetValue = if (isValid) {
            MaterialTheme.colorScheme.primary
        } else MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = Animation.colorSpec,
        label = "food_form_COLOR_ANIMATION"
    )

    Box(
        modifier = Modifier
            .width(24.dp)
            .height(6.dp)
            .scale(scale)
            .background(
                color = color,
                shape = CircleShape
            )
    )
}