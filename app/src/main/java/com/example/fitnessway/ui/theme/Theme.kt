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
    secondary = WarmOrange,
    tertiary = CoralPink,
    background = BlackBackground,
    primaryContainer = AreaContainerPrimaryDark,
    secondaryContainer = AreaContainerSecondaryDark,
    surface = BlackBackground,
    surfaceVariant = StormCloud,
    onSurfaceVariant = SilverMist,
    surfaceTint = LightCoal,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = WhiteFont,
    onSurface = WhiteFont,
    inverseSurface = SilverMist,
    inverseOnSurface = StormCloud,
    error = ErrorRed
)

private val LightColorScheme = lightColorScheme(
    primary = EmeraldDarker,
    secondary = DeepOrange,
    tertiary = VibrantPink,
    background = WhiteBackground,
    primaryContainer = AreaContainerPrimaryLight,
    secondaryContainer = AreaContainerSecondaryLight,
    surface = WhiteBackground,
    surfaceVariant = SilverMist,
    onSurfaceVariant = StormCloud,
    surfaceTint = WhiteCloud,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = BlackFont,
    onSurface = BlackFont,
    inverseSurface = StormCloud,
    inverseOnSurface = SilverMist,
    error = ErrorRed,
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