package com.example.fitnessway.feature.lists.screen.create.food.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fitnessway.ui.theme.WhiteFont
import com.example.fitnessway.ui.theme.robotoSerifFamily
import com.example.fitnessway.util.Animation

@Composable
fun NextButton(
    onClick: () -> Unit,
    enabled: Boolean = false,
    isSubmitting: Boolean,
    text: String,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        content = {
            val bgColor by animateColorAsState(
                targetValue = if (enabled && !isSubmitting) MaterialTheme.colorScheme.primary else {
                    MaterialTheme.colorScheme.surfaceVariant
                },
                animationSpec = Animation.colorSpec,
                label = "CreateFoodAnimationBackgroundColor"
            )

            val textColor = if (enabled) WhiteFont else WhiteFont.copy(0.3f)

            TextButton(
                onClick = onClick,
                enabled = enabled && !isSubmitting,
                contentPadding = PaddingValues(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max)
                    .background(
                        color = bgColor,
                        shape = CircleShape
                    )
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        strokeWidth = 2.dp,
                        modifier = Modifier.fillMaxHeight()
                    )
                } else {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        fontFamily = robotoSerifFamily,
                        color = textColor
                    )
                }
            }
        }
    )
}