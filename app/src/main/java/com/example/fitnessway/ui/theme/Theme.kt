package com.example.fitnessway.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// - background: The main app background color
// - surface: Background for cards, sheets, dialogs
// - onBackground: Text/icons that appear on the background
// - onSurface: Text/icons that appear on surfaces
// - onPrimary/onSecondary/onTertiary: Text/icons that appear on colored buttons/elements

private val DarkColorScheme = darkColorScheme(
   primary = Emerald,
   secondary = CoralPink,
   tertiary = WarmOrange,
   background = BlackBackground,
   surface = BlackBackground,
   surfaceVariant = StormCloud, // Input Bgs
   onPrimary = Color.White,
   onSecondary = Color.White,
   onTertiary = Color.White,
   onBackground = WhiteFont,
   onSurface = WhiteFont,
)

private val LightColorScheme = lightColorScheme(
   primary = EmeraldDarker,
   secondary = VibrantPink,
   tertiary = DeepOrange,
   background = WhiteBackground,
   surface = WhiteBackground,
   surfaceVariant = SilverMist,  // Input Bgs
   onPrimary = Color.White,
   onSecondary = Color.White,
   onTertiary = Color.White,
   onBackground = BlackFont,
   onSurface = BlackFont,
)

@Composable
fun FitnesswayTheme(
   darkTheme: Boolean = isSystemInDarkTheme(),
   // Dynamic color is available on Android 12+. If true, the app will use the system's Material
   // colors instead of the custom theme
   dynamicColor: Boolean = false,
   content: @Composable () -> Unit
) {
   val colorScheme = when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
         val context = LocalContext.current
         if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
   }

   MaterialTheme(
      colorScheme = colorScheme,
      typography = Typography,
      content = content
   )
}