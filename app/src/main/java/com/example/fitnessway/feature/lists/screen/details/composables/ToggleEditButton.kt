package com.example.fitnessway.feature.lists.screen.details.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.fitnessway.ui.theme.robotoSerifFamily

@Composable
fun ToggleEditButton(
    text: String,
    onClick: () -> Unit,
    background: Color? = MaterialTheme.colorScheme.primary
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(
                color = background ?: MaterialTheme.colorScheme.primary,
                shape = CircleShape
            )
            .clickable(
                onClick = onClick
            )
            .padding(
                horizontal = 14.dp, // vertical * 1.75
                vertical = 8.dp
            ),
        content = {
            Text(
                text = text,
                fontFamily = robotoSerifFamily,
                style = MaterialTheme.typography.bodySmall
            )
        }
    )
}