package com.example.fitnessway.feature.home.screen.main.composables

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fitnessway.R
import com.example.fitnessway.ui.shared.Clickables
import com.example.fitnessway.ui.theme.FitnesswayTheme
import com.example.fitnessway.ui.theme.robotoSerifFamily
import com.example.fitnessway.ui.shared.Structure.AppIconButtonSource

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

            Clickables.AppIconButton(
                onClick = onToggleFoodLogsVisibility,
                contentDescription = "View Food Logs",
                icon = AppIconButtonSource.Resource(R.drawable.scroll)
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewHeader() {
    FitnesswayTheme {
        HomeHeader(
            onToggleFoodLogsVisibility = {},
            date = "Today"
        )
    }
}