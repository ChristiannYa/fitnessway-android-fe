package com.example.fitnessway.ui.shared

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

@Composable
fun NotFoundText(text: String = "Data not found") {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onBackground.copy(0.7f),
        modifier = Modifier.fillMaxWidth(),
    )
}