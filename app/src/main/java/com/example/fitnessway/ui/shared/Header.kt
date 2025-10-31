package com.example.fitnessway.ui.shared

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment

@Composable
fun Header(
    onBackClick: () -> Unit,
    title: String? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        content = {
            IconButton(
                onClick = onBackClick,
                content = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            )

            if (title != null) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    )
}