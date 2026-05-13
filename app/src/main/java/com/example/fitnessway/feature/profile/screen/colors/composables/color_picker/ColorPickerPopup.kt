package com.example.fitnessway.feature.profile.screen.colors.composables.color_picker

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.example.fitnessway.ui.shared.Clickables
import com.example.fitnessway.ui.shared.Structure
import com.example.fitnessway.ui.theme.AppModifiers.areaContainer
import com.example.fitnessway.util.Animation
import com.example.fitnessway.util.PopupOrigin
import com.example.fitnessway.util.Ui
import com.example.fitnessway.util.extensions.isValidHexColor
import com.example.fitnessway.util.extensions.toHsv
import com.example.fitnessway.util.extensions.toPercentage

@Composable
fun ColorPickerPopup(
    isVisible: Boolean,
    initialHex: String,
    onSetHex: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val initialColor: Color = remember(initialHex) { Color("#$initialHex".toColorInt()) }
    var color: Color by remember(initialHex) { mutableStateOf(initialColor) }
    var hex: String by remember(initialHex) { mutableStateOf(initialHex) }
    var typedHex: String by remember(initialHex) { mutableStateOf(initialHex) }
    var brightness: Float by remember(initialHex) { mutableFloatStateOf(initialColor.toHsv()[2]) }


    LaunchedEffect(brightness, hex) {
        val newColor = color.toHsv().let { hsv ->
            Color.hsv(hsv[0], hsv[1], brightness)
        }
        val newHex = "%06X".format(newColor.toArgb() and 0xFFFFFF)

        color = newColor
        typedHex = newHex
        hex = newHex
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = Animation.ComposableTransition.ScaleWithSpring.enter(PopupOrigin.CENTER),
        exit = Animation.ComposableTransition.ScaleWithSpring.exit(PopupOrigin.CENTER),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .width(Ui.Measurements.POP_UP_CONTAINER_WIDTH)
                .areaContainer(
                    areaColor = MaterialTheme.colorScheme.surfaceVariant
                )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val colorWheelSize = Ui.Measurements.POP_UP_CONTAINER_WIDTH.times(0.5f)

                SelectedColor(
                    wheelSize = colorWheelSize,
                    color = color,
                    hex = typedHex,
                    onHexChange = {
                        typedHex = it

                        if (it.isValidHexColor()) {
                            val newColor = Color("#$it".toColorInt())

                            color = newColor
                            hex = it
                            brightness = newColor.toHsv()[2]
                        }
                    }
                )

                ColorWheel(
                    initialHex = hex,
                    brightness = brightness,
                    onColorSelected = { c, h -> color = c; hex = h },
                    modifier = Modifier.size(colorWheelSize)
                )

                BrightnessSlider(brightness) { brightness = it }
            }

            Clickables.DoneButton(
                enabled = typedHex.isValidHexColor(),
                isLoading = false,
                onClick = { onSetHex(typedHex) },
                modifier = Modifier.align(Alignment.TopEnd)
            )
        }
    }
}

@Composable
private fun SelectedColor(
    wheelSize: Dp,
    color: Color,
    hex: String,
    onHexChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.width(wheelSize.times(0.9f))) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                Modifier
                    .size(MaterialTheme.typography.bodyMedium.fontSize.value.dp.times(1.2f))
                    .background(color, CircleShape)
            )

            SelectedColorTextField(hex, onHexChange)
        }
    }
}

@Composable
private fun SelectedColorTextField(
    initialHex: String,
    onValueChange: (String) -> Unit
) {
    val hexValue = Ui.InputUi.rememberTextFieldValueWithCursor(initialHex)

    BasicTextField(
        value = hexValue.value,
        textStyle = Ui.InputUi.getTextStyle(textAlign = TextAlign.Center),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        enabled = true,
        singleLine = true,
        onValueChange = {
            val truncated = it.text.take(6).uppercase()

            hexValue.value = it.copy(
                text = truncated,
                selection = TextRange(truncated.length.coerceAtMost(it.selection.end))
            )

            onValueChange(truncated)
        },
        modifier = Modifier
            .clip(Ui.InputUi.shape)
            .width(120.dp)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = Ui.InputUi.shape
            )
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(Ui.InputUi.padding)
        ) {
            it()
        }
    }
}

@Composable
private fun BrightnessSlider(
    brightness: Float,
    onValueChange: (Float) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Slider(brightness, onValueChange)

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Structure.AppIconDynamic(
                source = Structure.AppIconSource.Vector(Icons.Default.WbSunny),
                modifier = Modifier.height(MaterialTheme.typography.labelLarge.fontSize.value.dp.times(1.2f))
            )

            Text(
                text = "${brightness.toPercentage()}%",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}