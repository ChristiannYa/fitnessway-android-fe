package com.example.fitnessway.ui.theme

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object AppModifiers {
    enum class AreaContainerSize(
        val cornerRadius: Dp,
        val padding: Dp
    ) {
        LARGE(16.dp, 18.dp),
        MEDIUM(14.dp, 16.dp),
        SMALL(12.dp, 14.dp)
    }

    @Composable
    fun Modifier.areaContainer(
        size: AreaContainerSize = AreaContainerSize.MEDIUM,
        areaColor: Color = MaterialTheme.colorScheme.primaryContainer,
        shape: RoundedCornerShape = RoundedCornerShape(size.cornerRadius),
        showsIndication: Boolean = false,
        onClickEnabled: Boolean = true,
        hugsContent: Boolean = false,
        borderWidth: Dp = 1.dp,
        borderColor: Color = Color.Transparent,
        onClick: (() -> Unit)? = null
    ) = this
        .then(
            if (onClick != null) {
                Modifier
                    .clip(shape)
                    .clickable(
                        interactionSource = if (showsIndication) null else {
                            remember { MutableInteractionSource() }
                        },
                        indication = if (showsIndication) LocalIndication.current else null,
                        onClick = onClick,
                        enabled = onClickEnabled
                    )
            } else Modifier
        )
        .then(if (!hugsContent) Modifier.fillMaxWidth() else Modifier)
        .border(
            width = borderWidth,
            color = borderColor,
            shape = shape
        )
        .background(
            color = areaColor,
            shape = shape
        )
        .padding(size.padding)


    @Composable
    fun Modifier.areaContainerLarge(
        areaColor: Color = MaterialTheme.colorScheme.primaryContainer,
        shape: RoundedCornerShape = RoundedCornerShape(16.dp),
        showsIndication: Boolean = false,
        onClickEnabled: Boolean = true,
        onClick: (() -> Unit)? = null
    ) = this
        .then(
            if (onClick != null) {
                Modifier
                    .clip(shape)
                    .clickable(
                        interactionSource = if (showsIndication) null else {
                            remember { MutableInteractionSource() }
                        },
                        indication = if (showsIndication) LocalIndication.current else null,
                        onClick = onClick,
                        enabled = onClickEnabled
                    )
            } else Modifier
        )
        .fillMaxWidth()
        .background(
            color = areaColor,
            shape = shape
        )
        .padding(18.dp)

    @Composable
    fun Modifier.areaContainerMedium(
        areaColor: Color = MaterialTheme.colorScheme.primaryContainer,
        shape: RoundedCornerShape = RoundedCornerShape(14.dp)
    ) = this
        .fillMaxWidth()
        .background(
            color = areaColor,
            shape = shape
        )
        .padding(16.dp)

    @Composable
    fun Modifier.areaContainerSmall(
        areaColor: Color = MaterialTheme.colorScheme.primaryContainer,
        shape: RoundedCornerShape = RoundedCornerShape(12.dp),
        showsIndication: Boolean = true,
        hugsContent: Boolean = false,
        onClick: (() -> Unit)? = null,
    ) = this
        .then(
            if (onClick != null) {
                Modifier
                    .clip(shape)
                    .clickable(
                        interactionSource = if (showsIndication) null else {
                            remember { MutableInteractionSource() }
                        },
                        indication = if (showsIndication) LocalIndication.current else null,
                        onClick = onClick
                    )
            } else Modifier
        )
        .then(if (!hugsContent) Modifier.fillMaxWidth() else Modifier)
        .background(
            color = areaColor,
            shape = shape
        )
        .padding(14.dp)

    @Composable
    fun Modifier.blurPremiumItem(
        shouldBlur: Boolean
    ) = this
        .blur(if (!shouldBlur) 0.dp else 2.dp)

}