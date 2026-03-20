package com.example.fitnessway.ui.shared

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fitnessway.util.Animation

@Composable
fun FormProgress(
    currentStep: Int,
    stepValidations: List<Boolean>,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
        modifier = modifier.fillMaxWidth()
    ) {
        stepValidations.forEachIndexed { index, isValid ->
            ProgressIndicator(
                isCurrentStep = currentStep == index + 1,
                isValid = isValid,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ProgressIndicator(
    isCurrentStep: Boolean,
    isValid: Boolean,
    modifier: Modifier = Modifier
) {
    val color by animateColorAsState(
        targetValue = when {
            isValid -> MaterialTheme.colorScheme.primary
            isCurrentStep -> MaterialTheme.colorScheme.inverseSurface
            else -> MaterialTheme.colorScheme.surfaceVariant
        },
        animationSpec = Animation.colorSpec,
        label = "food_form_COLOR_ANIMATION"
    )

    Box(
        modifier = modifier
            .height(4.dp)
            .background(
                color = color,
                shape = CircleShape
            )
    )
}