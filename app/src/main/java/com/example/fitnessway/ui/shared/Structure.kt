package com.example.fitnessway.ui.shared

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fitnessway.ui.shared.Messages.NotFoundMessage
import com.example.fitnessway.util.Ui.Measurements.SCREEN_HORIZONTAL_PADDING

object Structure {
    @Composable
    fun Screen(
        header: (@Composable () -> Unit)? = null,
        usesInnerHorizontalPadding: Boolean = true,
        content: @Composable () -> Unit,
    ) {
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
                                .padding(
                                    start = horizontalPadding,
                                    end = horizontalPadding,
                                    top = 4.dp
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

    @Composable
    fun NotFoundScreen(
        onBackClick: () -> Unit,
        title: String? = null,
        message: String
    ) {
        Screen(
            header = {
                Header(
                    onBackClick = onBackClick,
                    title = title
                )
            }
        ) { NotFoundMessage(message) }
    }
}