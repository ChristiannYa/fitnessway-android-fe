package com.example.fitnessway.ui.theme

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object AppModifiers {
    enum class AreaContainerSize(
        val cornerRadius: Dp,
        val padding: Dp
    ) {
        LARGE(16.dp, 18.dp),
        MEDIUM(14.dp, 16.dp),
        SMALL(12.dp, 14.dp),
        EXTRA_SMALL(8.dp, 10.dp)
    }

    @Composable
    fun Modifier.areaContainer(
        size: AreaContainerSize = AreaContainerSize.LARGE,
        areaColor: Color = MaterialTheme.colorScheme.primaryContainer,
        shape: RoundedCornerShape = RoundedCornerShape(size.cornerRadius),
        borderWidth: Dp = 1.dp,
        borderColor: Color = Color.Transparent,
        isTapIndicationVisible: Boolean = false,
        isClickEnabled: Boolean = true,
        isContentHugged: Boolean = false,
        isBottomPaddingIncluded: Boolean = true,
        withPadding: Boolean = true,
        onClick: (() -> Unit)? = null
    ) = this
        .then(
            if (onClick != null) {
                Modifier
                    .clip(shape)
                    .tappable(
                        showsTap = isTapIndicationVisible,
                        enabled = isClickEnabled,
                        onClick = onClick
                    )
            } else Modifier
        )
        .then(if (!isContentHugged) Modifier.fillMaxWidth() else Modifier)
        .border(
            width = borderWidth,
            color = borderColor,
            shape = shape
        )
        .background(
            color = areaColor,
            shape = shape
        )
        .then(
            if (withPadding) {
                Modifier.padding(
                    top = size.padding,
                    bottom = if (isBottomPaddingIncluded) size.padding else 0.dp,
                    start = size.padding,
                    end = size.padding
                )
            } else Modifier
        )

    @Composable
    fun Modifier.foodContainer(
        onClick: (() -> Unit)? = null
    ) = this.areaContainer(
        size = AreaContainerSize.SMALL,
        borderColor = MaterialTheme.colorScheme.surfaceVariant,
        isTapIndicationVisible = true,
        isClickEnabled = onClick != null,
        onClick = { onClick?.let { it() } }
    )
}

@Composable
fun Modifier.tappable(
    enabled: Boolean = true,
    showsTap: Boolean = false,
    onClick: () -> Unit
) = this
    .clickable(
        interactionSource = if (showsTap) null else {
            remember { MutableInteractionSource() }
        },
        indication = if (showsTap) LocalIndication.current else null,
        enabled = enabled,
        onClick = onClick
    )

@Composable
fun Modifier.consumeTap(
    vararg pointerInputKeys: Any?,
    onTap: (() -> Unit)? = null
) = this.pointerInput(pointerInputKeys) {
    detectTapGestures { onTap?.let { it() } }
}