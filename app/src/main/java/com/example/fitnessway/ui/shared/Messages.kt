package com.example.fitnessway.ui.shared

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fitnessway.util.Animation

object Messages {
    @Composable
    fun NotFoundMessage(message: String = "Data not found") {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(0.8f),
            modifier = Modifier.fillMaxWidth(),
        )
    }

    @Composable
    fun NotFoundMessageAnimated(
        isVisible: Boolean,
        message: String,
        modifier: Modifier = Modifier
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = Animation.ComposableTransition.PopUpV2.enter,
            exit = Animation.ComposableTransition.PopUpV2.exit,
            modifier = modifier
        ) { NotFoundMessage(message) }
    }

    @Composable
    fun SuccessMessage(message: String) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            SuccessIcon()
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

    @Composable
    fun SuccessMessageAnimated(
        isVisible: Boolean,
        message: String,
        modifier: Modifier = Modifier
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = Animation.ComposableTransition.PopUpV2.enter,
            exit = Animation.ComposableTransition.PopUpV2.exit,
            modifier = modifier
        ) { SuccessMessage(message) }
    }
}