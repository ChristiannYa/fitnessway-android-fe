package com.example.fitnessway.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

private val FF = FontFamily.Serif
private val LS = 0.5.sp

private fun getLineHeight(fontSize: Int): TextUnit = (fontSize * 1.20).sp

// Set of Material typography styles to start with
val Typography = Typography(
    labelSmall = TextStyle(
        fontFamily = FF,
        fontSize = 11.sp,
        lineHeight = getLineHeight(11),
        letterSpacing = LS
    ),
    labelMedium = TextStyle(
        fontFamily = FF,
        fontSize = 12.sp,
        lineHeight = getLineHeight(12),
        letterSpacing = LS
    ),
    labelLarge = TextStyle(
        fontFamily = FF,
        fontSize = 13.sp,
        lineHeight = getLineHeight(13),
        letterSpacing = LS
    ),
    bodySmall = TextStyle(
        fontFamily = FF,
        fontSize = 14.sp,
        lineHeight = getLineHeight(14),
        letterSpacing = LS
    ),
    bodyMedium = TextStyle(
        fontFamily = FF,
        fontSize = 15.sp,
        lineHeight = getLineHeight(15),
        letterSpacing = LS
    ),
    bodyLarge = TextStyle(
        fontFamily = FF,
        fontSize = 16.sp,
        lineHeight = getLineHeight(16),
        letterSpacing = LS
    ),
    titleSmall = TextStyle(
        fontFamily = FF,
        fontSize = 17.sp,
        lineHeight = getLineHeight(17),
        letterSpacing = LS
    ),
    titleMedium = TextStyle(
        fontFamily = FF,
        fontSize = 18.sp,
        lineHeight = getLineHeight(18),
        letterSpacing = LS
    ),
    titleLarge = TextStyle(
        fontFamily = FF,
        fontSize = 19.sp,
        lineHeight = getLineHeight(19),
        letterSpacing = LS
    )
)