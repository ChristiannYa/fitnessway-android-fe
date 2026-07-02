package com.example.fitnessway.ui.shared

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.example.fitnessway.ui.theme.keyboardTapDismissal
import com.example.fitnessway.util.Ui.Measurements.SCREEN_HORIZONTAL_PADDING

@Composable
fun Screen(
    header: (@Composable () -> Unit)? = null,
    usesInnerHorizontalPadding: Boolean = true,
    content: @Composable (focusManager: FocusManager) -> Unit,
) {
    val focusManager = LocalFocusManager.current

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            val horizontalPadding = if (usesInnerHorizontalPadding) {
                SCREEN_HORIZONTAL_PADDING
            } else 0.dp

            header?.let { it() }

            Box(
                modifier = Modifier
                    .keyboardTapDismissal(header != null, usesInnerHorizontalPadding)
                    .padding(
                        start = horizontalPadding,
                        end = horizontalPadding,
                        top = 2.dp
                    ),
            ) { content(focusManager) }
        }
    }
}