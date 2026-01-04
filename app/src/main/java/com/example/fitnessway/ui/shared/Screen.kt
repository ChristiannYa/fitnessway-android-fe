package com.example.fitnessway.ui.shared

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.example.fitnessway.util.Ui.Measurements.SCREEN_HORIZONTAL_PADDING

@Composable
fun Screen(
    header: (@Composable () -> Unit)? = null,
    usesInnerHorizontalPadding: Boolean = true,
    content: @Composable (focusManager: FocusManager) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0

    Surface(
        modifier = Modifier.fillMaxSize(),
        content = {
            Column(
                modifier = Modifier.fillMaxSize(),
                content = {
                    val horizontalPadding = if (usesInnerHorizontalPadding) {
                        SCREEN_HORIZONTAL_PADDING
                    } else 0.dp

                    header?.let { it() }

                    Box(
                        modifier = Modifier
                            .pointerInput(isImeVisible) {
                                if (isImeVisible) {
                                    detectTapGestures(
                                        onTap = {
                                            focusManager.clearFocus()
                                        }
                                    )
                                }
                            }
                            .padding(
                                start = horizontalPadding,
                                end = horizontalPadding,
                                top = 4.dp
                            ),
                        content = {
                            content(focusManager)
                        }
                    )
                }
            )
        }
    )
}