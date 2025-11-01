package com.example.fitnessway.feature.home.screen.main.foodselection.foodlog.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fitnessway.ui.theme.WhiteFont
import com.example.fitnessway.ui.theme.robotoSerifFamily

@Composable
fun EditionButtons() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth(),
        content = {
            EditionButton(
                text = "Edit",
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                onClick = {},
                modifier = Modifier
                    .weight(1f)
            )
            EditionButton(
                text = "Log",
                backgroundColor = MaterialTheme.colorScheme.primary,
                textColor = WhiteFont,
                onClick = {},
                modifier = Modifier
                    .weight(1f)
            )
        }
    )
}

@Composable
fun EditionButton(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    onClick: () -> Unit,
    modifier: Modifier
) {
    val radius = 10.dp

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(radius))
            .clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            )
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(radius)
            )
            .padding(vertical = 10.dp, horizontal = 8.dp),
        content = {
            Text(
                text = text,
                fontFamily = robotoSerifFamily,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                fontWeight = FontWeight.Medium,
                color = textColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    )
}