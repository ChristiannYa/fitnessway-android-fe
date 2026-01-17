package com.example.fitnessway.ui.shared

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
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
    fun NotFoundMessageWithRetryAnimated(
        isVisible: Boolean,
        message: String,
        onRetry: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = Animation.ComposableTransition.PopUpV2.enter,
            exit = Animation.ComposableTransition.PopUpV2.exit,
            modifier = modifier
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = modifier.fillMaxWidth()
            ) {
                NotFoundMessage(message)

                IconButton(
                    onClick = onRetry,
                    colors = IconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceTint,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceTint,
                        disabledContentColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.primary,
                    ),
                    modifier = Modifier.size(42.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Autorenew,
                        contentDescription = null
                    )
                }
            }
        }
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