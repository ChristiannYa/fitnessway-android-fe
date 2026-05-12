package com.example.fitnessway.ui.shared

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SuccessIcon(
    tint: Color = MaterialTheme.colorScheme.primary,
    size: Dp = 28.dp,
    modifier: Modifier = Modifier
) {
    Icon(
        imageVector = Icons.Default.CheckCircle,
        contentDescription = null,
        tint = tint,
        modifier = modifier.size(size)
    )
}