package com.example.fitnessway.feature.home.screen.main.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fitnessway.R
import com.example.fitnessway.ui.shared.Clickables
import com.example.fitnessway.ui.shared.Structure.AppIconSource
import com.example.fitnessway.ui.theme.robotoSerifFamily

@Composable
fun HomeHeader(
    onToggleFoodLogsVisibility: () -> Unit,
    date: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Fitnessway",
            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
            fontFamily = robotoSerifFamily,
            color = MaterialTheme.colorScheme.onBackground
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = date,
                style = MaterialTheme.typography.bodyLarge
            )

            Clickables.AppPngIconButton(
                onClick = onToggleFoodLogsVisibility,
                contentDescription = "View Food Logs",
                icon = AppIconSource.Resource(R.drawable.scroll)
            )
        }
    }
}