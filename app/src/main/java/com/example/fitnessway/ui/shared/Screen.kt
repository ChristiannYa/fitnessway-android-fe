package com.example.fitnessway.ui.shared

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
    Surface(
        modifier = Modifier.fillMaxSize(),
        content = {
            Column {
                header?.let { it() }
                Box(
                    modifier = Modifier
                        .padding(
                            horizontal = 16.dp,
                            vertical = if (header != null && isMainScreen == false) 16.dp else 0.dp
                        ),
                    content = {
                        Column {
                            content()
                        }
                    }
                )
            }
        }
    )
}