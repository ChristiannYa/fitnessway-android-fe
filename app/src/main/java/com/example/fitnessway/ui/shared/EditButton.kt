package com.example.fitnessway.ui.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fitnessway.ui.theme.robotoSerifFamily

@Composable
fun EditButton(
    onClick: () -> Unit,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    text: String = "Edit",
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(CircleShape)
            .background(
                color = backgroundColor,
                shape = CircleShape
            )
            .clickable(
                onClick = onClick
            )
            .padding(
                horizontal = 12.dp,
                vertical = 6.dp
            ),
        content = {
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                fontFamily = robotoSerifFamily,
            )
        }
    )
}