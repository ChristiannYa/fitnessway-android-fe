package com.example.fitnessway.ui.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fitnessway.util.Ui

object Loading {
    @Composable
    fun LoadingArea(
        text: String? = null
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (text != null) {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                CircularProgressIndicator(
                    strokeWidth = Ui.Measurements.PROGRESS_INDICATOR_WIDTH,
                    modifier = Modifier.size(Ui.Measurements.PROGRESS_INDICATOR_SIZE)
                )
            }
        }
    }
}