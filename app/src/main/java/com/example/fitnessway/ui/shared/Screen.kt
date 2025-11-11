package com.example.fitnessway.ui.shared

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Screen(
    header: (@Composable () -> Unit)? = null,
    isMainScreen: Boolean? = false,
    content: @Composable () -> Unit,
) {
    val hasPadding = header != null && isMainScreen == false

    Surface(
        modifier = Modifier.fillMaxSize(),
        content = {
            Column {
                header?.let { it() }
                Box(
                    modifier = Modifier
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = if (hasPadding) 8.dp else 0.dp,
                            bottom = if (hasPadding) 16.dp else 0.dp,
                        ),
                    content = {
                        content()
                    }
                )
            }
        }
    )
}