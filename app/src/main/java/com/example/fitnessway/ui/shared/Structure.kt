package com.example.fitnessway.ui.shared

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.fitnessway.ui.shared.Messages.NotFoundMessage
import com.example.fitnessway.ui.theme.WhiteFont
import com.example.fitnessway.ui.theme.robotoSerifFamily
import com.example.fitnessway.util.Animation
import com.example.fitnessway.util.Ui.Measurements.SCREEN_HORIZONTAL_PADDING

object Structure {
    sealed interface AppIconButtonSource {
        data class Resource(@get:DrawableRes val id: Int) : AppIconButtonSource
        data class Vector(val imageVector: ImageVector) : AppIconButtonSource
    }

    @Composable
    fun AppIconDynamic(
        source: AppIconButtonSource,
        contentDescription: String? = null,
        tint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier: Modifier = Modifier
    ) {
        when (source) {
            is AppIconButtonSource.Resource -> {
                Icon(
                    painter = painterResource(source.id),
                    contentDescription = contentDescription,
                    tint = tint,
                    modifier = modifier
                )
            }

            is AppIconButtonSource.Vector -> {
                Icon(
                    imageVector = source.imageVector,
                    contentDescription = contentDescription,
                    tint = tint,
                    modifier = modifier
                )
            }
        }
    }

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

    class MoreOptionsState {
        var isVisible by mutableStateOf(false)
            private set

        fun show() {
            isVisible = true
        }

        fun hide() {
            isVisible = false
        }

        fun toggle() {
            isVisible = !isVisible
        }
    }

    data class MoreOptionsConfig(
        val text: String,
        val onClick: () -> Unit,
        val icon: AppIconButtonSource? = null,
        val iconTint: Color? = null,
        val iconModifier: Modifier = Modifier,
        val backgroundColor: Color? = null,
        val textColor: Color? = null
    )

    @Composable
    fun rememberMoreOptionsState(): MoreOptionsState {
        return remember { MoreOptionsState() }
    }

    @Composable
    fun MoreOptions(
        state: MoreOptionsState,
        vararg options: MoreOptionsConfig,
        modifier: Modifier = Modifier
    ) {
        AnimatedVisibility(
            visible = state.isVisible,
            enter = Animation.ComposableTransition.PopUpV1.enter,
            exit = Animation.ComposableTransition.PopUpV1.exit,
            modifier = modifier
        ) {
            val shape = 16.dp
            val space = 5.dp

            Box(
                modifier = Modifier
                    .width(IntrinsicSize.Max)
                    .clip(RoundedCornerShape(shape))
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(shape)
                    )
                    .padding(space)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(space),
                    modifier = Modifier
                        .clip(RoundedCornerShape(shape - 4.dp))
                ) {
                    options.forEach {
                        val hasIcon = it.icon != null

                        Row(
                            horizontalArrangement = if (hasIcon) Arrangement.Start else {
                                Arrangement.Center
                            },
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    it.backgroundColor ?: MaterialTheme.colorScheme.surfaceTint
                                )
                                .clickable(onClick = it.onClick)
                                .padding(space.times(3))
                        ) {
                            if (hasIcon) {
                                AppIconDynamic(
                                    source = it.icon,
                                    tint = it.iconTint
                                        ?: MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = it.iconModifier
                                )

                                Spacer(modifier = Modifier.width(space))
                            }

                            Text(
                                text = it.text,
                                style = MaterialTheme.typography.labelLarge,
                                color = it.textColor ?: WhiteFont,
                                fontFamily = robotoSerifFamily
                            )
                        }
                    }
                }
            }
        }
    }
}