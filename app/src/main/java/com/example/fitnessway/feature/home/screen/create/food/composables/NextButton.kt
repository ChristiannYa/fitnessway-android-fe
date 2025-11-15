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
import androidx.compose.ui.unit.dp
import com.example.fitnessway.ui.theme.WhiteFont
import com.example.fitnessway.ui.theme.robotoSerifFamily

@Composable
fun NextButton(onClick: () -> Unit, text: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        content = {
            TextButton(
                onClick = onClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    ),
                content = {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = robotoSerifFamily,
                        color = WhiteFont
                    )
                }
            )
        }
    )
}