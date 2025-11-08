package com.example.fitnessway.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp

private val FF = FontFamily.Serif
private val LS = 0.5.sp

// Set of Material typography styles to start with
val Typography = Typography(
    labelSmall = TextStyle(
        fontFamily = FF,
        fontSize = 11.sp,
        lineHeight = 16.5.sp,
        letterSpacing = LS
    ),
    labelMedium = TextStyle(
        fontFamily = FF,
        fontSize = 12.sp,
        lineHeight = 18.sp,
        letterSpacing = LS
    ),
    labelLarge = TextStyle(
        fontFamily = FF,
        fontSize = 13.sp,
        lineHeight = 19.5.sp,
        letterSpacing = LS
    ),
    bodySmall = TextStyle(
        fontFamily = FF,
        fontSize = 14.sp,
        lineHeight = 21.sp,
        letterSpacing = LS
    ),
    bodyMedium = TextStyle(
        fontFamily = FF,
        fontSize = 15.sp,
        lineHeight = 22.5.sp,
        letterSpacing = LS
    ),
    bodyLarge = TextStyle(
        fontFamily = FF,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = LS
    ),
    titleSmall = TextStyle(
        fontFamily = FF,
        fontSize = 17.sp,
        lineHeight = 25.5.sp,
        letterSpacing = LS
    ),
    titleMedium = TextStyle(
        fontFamily = FF,
        fontSize = 18.sp,
        lineHeight = 27.sp,
        letterSpacing = LS
    ),
    titleLarge = TextStyle(
        fontFamily = FF,
        fontSize = 19.sp,
        lineHeight = 28.5.sp,
        letterSpacing = LS
    )
)