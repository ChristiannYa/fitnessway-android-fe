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
import com.example.fitnessway.R
import com.example.fitnessway.ui.shared.Clickables
import com.example.fitnessway.ui.theme.FitnesswayTheme
import com.example.fitnessway.ui.theme.robotoSerifFamily

@Composable
fun HomeHeader(
    onToggleCreateMenuVisibility: () -> Unit
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

        Clickables.AppIconButton(
            onClick = {},
            contentDescription = "View Food Logs",
            icon = Clickables.AppIconButtonSource.Resource(R.drawable.scroll)
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewHeader() {
    FitnesswayTheme {
        HomeHeader(
            onToggleCreateMenuVisibility = {}
        )
    }
}