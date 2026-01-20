package com.example.fitnessway.feature.lists.screen.create.food.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.fitnessway.util.Animation

@Composable
fun FormProgressIndicator(
    currentStep: Int,
    isStepOneValid: Boolean,
    isStepTwoValid: Boolean,
    isStepThreeValid: Boolean,
    isStepFourValid: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
        modifier = modifier,
        content = {
            ProgressIndicator(
                isCurrentStep = currentStep == 1,
                isValid = isStepOneValid,
                activeColor = MaterialTheme.colorScheme.inverseSurface,
            )
            ProgressIndicator(
                isCurrentStep = currentStep == 2,
                isValid = isStepTwoValid,
                activeColor = MaterialTheme.colorScheme.secondary,
            )
            ProgressIndicator(
                isCurrentStep = currentStep == 3,
                isValid = isStepThreeValid,
                activeColor = MaterialTheme.colorScheme.tertiary
            )
            ProgressIndicator(
                isCurrentStep = currentStep == 4,
                isValid = isStepFourValid,
                activeColor = MaterialTheme.colorScheme.primary
            )
        }
    )
}

@Composable
private fun ProgressIndicator(
    isCurrentStep: Boolean,
    isValid: Boolean,
    activeColor: Color
) {
    val scale by animateFloatAsState(
        targetValue = if (isCurrentStep)
            1f
        else
            0.6f,
        animationSpec = tween(durationMillis = 300),
        label = "food_form_SCALE_ANIMATION"
    )

    val color by animateColorAsState(
        targetValue = if (isValid)
            activeColor
        else
            MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = Animation.colorSpec,
        label = "food_form_COLOR_ANIMATION"
    )

    Box(
        modifier = Modifier
            .size(16.dp)
            .scale(scale)
            .background(
                color = color,
                shape = CircleShape
            )
    )
}