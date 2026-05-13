package com.example.fitnessway.feature.profile.screen.colors.composables.color_picker

import android.graphics.Color.colorToHSV
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

private data class ColorPickResult(
    val clampedPos: Offset,
    val color: Color,
    val hex: String
)

@Composable
fun ColorWheel(
    initialHex: String,
    brightness: Float,
    onColorSelected: (Color, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectorPos by remember { mutableStateOf(Offset.Unspecified) }
    var canvasRadius by remember { mutableFloatStateOf(0f) }
    var canvasCenter by remember { mutableStateOf(Offset.Zero) }

    val initialHsv = remember(initialHex) {
        val floatArray = FloatArray(3)
        colorToHSV("#$initialHex".toColorInt(), floatArray)
        floatArray
    }

    // Handles case when a user types in a valid hex color
    LaunchedEffect(initialHex) {
        selectorPos = Offset.Unspecified
    }

    fun pickColor(pos: Offset): ColorPickResult {
        val dx = pos.x - canvasCenter.x
        val dy = pos.y - canvasCenter.y
        val dist = sqrt(dx * dx + dy * dy)

        val saturation = (dist / canvasRadius).coerceIn(0f, 1f)
        var hue = atan2(dy, dx) * (180f / PI.toFloat())
        if (hue < 0f) hue += 360f

        val clampedPos = if (dist <= canvasRadius) pos
        else Offset(
            x = canvasCenter.x + dx / dist * canvasRadius,
            y = canvasCenter.y + dy / dist * canvasRadius
        )

        val color = Color.hsv(hue, saturation, brightness)
        val hex = "%06X".format(color.toArgb() and 0xFFFFFF)

        return ColorPickResult(clampedPos, color, hex)
    }

    Canvas(
        modifier = modifier
            .pointerInput(brightness) {
                detectTapGestures { offset ->
                    val result = pickColor(offset)
                    selectorPos = result.clampedPos
                    onColorSelected(result.color, result.hex)
                }
            }
            .pointerInput(brightness) {
                detectDragGestures { change, _ ->
                    val result = pickColor(change.position)
                    selectorPos = result.clampedPos
                    onColorSelected(result.color, result.hex)
                }
            }
    ) {
        canvasRadius = this.size.minDimension / 2f
        canvasCenter = this.center

        // Draw hue sweep (outer ring)
        drawCircle(
            brush = Brush.sweepGradient(
                colors = listOf(
                    Color.Red, Color.Yellow, Color.Green,
                    Color.Cyan, Color.Blue, Color.Magenta, Color.Red
                ),
                center = this.center
            ),
            radius = canvasRadius
        )

        // Overlay radial white
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.White, Color.Transparent),
                center = this.center,
                radius = canvasRadius
            ),
            radius = canvasRadius
        )

        // Darken for brightness
        if (brightness < 1f) {
            drawCircle(
                color = Color.Black.copy(alpha = 1f - brightness),
                radius = canvasRadius
            )
        }

        // Set initial selector position once canvas is measured
        if (selectorPos == Offset.Unspecified && canvasRadius > 0f) {
            val hueRad = initialHsv[0] * (PI.toFloat() / 180f)
            val dist = initialHsv[1] * canvasRadius
            selectorPos = Offset(
                x = canvasCenter.x + dist * cos(hueRad),
                y = canvasCenter.y + dist * sin(hueRad)
            )
        }

        // Draw selector ring
        if (selectorPos != Offset.Unspecified) {
            drawCircle(color = Color.White, radius = 6.dp.toPx(), center = selectorPos)
            drawCircle(
                color = Color.Black, radius = 6.dp.toPx(), center = selectorPos,
                style = Stroke(width = 2.dp.toPx())
            )
        }
    }
}