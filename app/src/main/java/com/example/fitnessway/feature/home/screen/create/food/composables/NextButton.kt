package com.example.fitnessway.feature.home.screen.create.food.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fitnessway.ui.theme.WhiteFont
import com.example.fitnessway.ui.theme.robotoSerifFamily

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
            val bgColor = if (enabled) MaterialTheme.colorScheme.primary else {
                MaterialTheme.colorScheme.surfaceVariant
            }

            val textDynamic = if (isSubmitting) "Creating Food..." else text
            val textColor = if (enabled) WhiteFont else WhiteFont.copy(0.3f)

            TextButton(
                onClick = onClick,
                enabled = enabled && !isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = bgColor,
                        shape = CircleShape
                    ),
                content = {
                    Text(
                        text = textDynamic,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        fontFamily = robotoSerifFamily,
                        color = textColor
                    )
                }
            )
        }
    )
}