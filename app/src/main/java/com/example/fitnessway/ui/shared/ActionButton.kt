package com.example.fitnessway.ui.shared

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
import com.example.fitnessway.ui.theme.WhiteFont
import com.example.fitnessway.ui.theme.robotoSerifFamily

@Composable
fun ActionButton(
    text: String,
    onClick: () -> Unit,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    enabled: Boolean = true
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(
                color = backgroundColor,
                shape = CircleShape
            )
            .clickable(
                onClick = onClick,
                enabled = enabled
            )
            .padding(
                horizontal = 14.dp, // vertical * 1.75
                vertical = 8.dp
            ),
        content = {
            Text(
                text = text,
                fontFamily = robotoSerifFamily,
                style = MaterialTheme.typography.bodySmall,
                color = WhiteFont.copy(if (enabled) 1f else 0.3f)
            )
        }
    )
}