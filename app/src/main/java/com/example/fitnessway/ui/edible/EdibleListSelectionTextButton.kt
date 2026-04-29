package com.example.fitnessway.ui.edible

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.example.fitnessway.ui.theme.AppModifiers
import com.example.fitnessway.ui.theme.AppModifiers.areaContainer
import com.example.fitnessway.ui.theme.robotoSerifFamily

@Composable
fun EdibleListSelectionTextButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .areaContainer(
                size = AppModifiers.AreaContainerSize.EXTRA_SMALL,
                areaColor = Color.Transparent,
                hugsContent = true,
                onClick = onClick
            )
    ) {
        val color = MaterialTheme.colorScheme.onBackground

        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
            color = if (isSelected) color else color.copy(0.5f),
            fontFamily = robotoSerifFamily
        )
    }
}