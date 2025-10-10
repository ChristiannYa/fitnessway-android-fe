package com.example.fitnessway.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.example.fitnessway.R

val robotoSerifFamily = FontFamily(
   Font(
      R.font.robotoserif_regular,
      FontWeight.Normal
   ),
   Font(
      R.font.robotoserif_italic,
      FontWeight.Normal,
      FontStyle.Italic,
   ),
   Font(
      R.font.robotoserif_medium,
      FontWeight.Medium
   ),
   Font(
      R.font.robotoserif_bold,
      FontWeight.Bold
   ),
   Font(
      R.font.robotoserif_bolditalic,
      FontWeight.Bold,
      FontStyle.Italic,
   )
)