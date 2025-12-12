package com.example.fitnessway.ui.shared

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.fitnessway.util.UiMeasures

@Composable
fun Screen(
    header: (@Composable () -> Unit)? = null,
    isMainScreen: Boolean? = false,
    usesInnerHorizontalPadding: Boolean = true,
    content: @Composable () -> Unit,
) {
    val hasPadding = header != null && isMainScreen == false
    val localDensity = LocalDensity.current
    val bottomInsetPadding = WindowInsets.navigationBars.getBottom(localDensity) / 2
    val bottomPaddingDynamic = if (isMainScreen == true) 0.dp else bottomInsetPadding.dp

    Surface(
        modifier = Modifier.fillMaxSize(),
        content = {
            Column(
                modifier = Modifier.fillMaxSize(),
                content = {
                    val horizontalPadding = if (usesInnerHorizontalPadding) {
                        UiMeasures.SCREEN_HORIZONTAL_PADDING
                    } else 0.dp

                    header?.let { it() }

                    Box(
                        modifier = Modifier
                            .padding(
                                start = horizontalPadding,
                                end = horizontalPadding,
                                top = if (hasPadding) 4.dp else 2.dp,
                                bottom = bottomPaddingDynamic,
                            ),
                        content = {
                            content()
                        }
                    )
                }
            )
        }
    )
}