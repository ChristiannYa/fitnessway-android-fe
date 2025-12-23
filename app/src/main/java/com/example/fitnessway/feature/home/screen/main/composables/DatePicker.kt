package com.example.fitnessway.feature.home.screen.main.composables

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.fitnessway.ui.shared.Clickables
import com.example.fitnessway.ui.theme.FitnesswayTheme
import com.example.fitnessway.util.Ui

@Composable
fun DatePicker(
    date: String,
    onNextDay: () -> Unit,
    onPrevDay: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Clickables.AppIconButton(
            onClick = onPrevDay,
            contentDescription = "View Previous Day's Details",
            icon = Clickables.AppIconButtonSource.Vector(Icons.AutoMirrored.Filled.ArrowBack)
        )

        Ui.AppLabel(
            text = date,
            textStyle = MaterialTheme.typography.bodySmall
        )

        Clickables.AppIconButton(
            onClick = onNextDay,
            contentDescription = "View Next Day's Details",
            icon = Clickables.AppIconButtonSource.Vector(Icons.AutoMirrored.Filled.ArrowForward)
        )
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
)
@Composable
fun DatePickerPreview() {
    FitnesswayTheme {
        DatePicker(
            date = "Yesterday",
            onNextDay = {},
            onPrevDay = {}
        )
    }
}