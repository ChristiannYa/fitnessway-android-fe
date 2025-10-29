package com.example.fitnessway.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val Typography = Typography(
   bodyMedium = TextStyle(
      fontFamily = FontFamily.Serif,
      fontSize = 14.sp,
      lineHeight = 21.sp,
      letterSpacing = 0.5.sp
   ),
   bodyLarge = TextStyle(
      fontFamily = FontFamily.Serif,
      fontWeight = FontWeight.Normal,
      fontSize = 16.sp,
      lineHeight = 24.sp,
      letterSpacing = 0.5.sp
   )
)