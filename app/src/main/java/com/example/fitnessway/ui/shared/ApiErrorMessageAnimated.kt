package com.example.fitnessway.ui.shared

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import com.example.fitnessway.util.Animation

@Composable
fun ApiErrorMessageAnimated(
    isVisible: Boolean,
    errorMessage: String
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = Animation.Transition.PopUpV2.enter,
        exit = Animation.Transition.PopUpV2.exit,
        content = {
            ApiErrorMessage(errorMessage)
        }
    )
}