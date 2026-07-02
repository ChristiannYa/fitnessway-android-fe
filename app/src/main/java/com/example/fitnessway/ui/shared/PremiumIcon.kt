package com.example.fitnessway.ui.shared

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun PremiumIcon(
    size: Dp = 14.dp,
    modifier: Modifier = Modifier,
) {
    Icon(
        imageVector = AppVectors.premium,
        contentDescription = null,
        modifier = modifier.size(size),
        tint = MaterialTheme.colorScheme.primary
    )
}