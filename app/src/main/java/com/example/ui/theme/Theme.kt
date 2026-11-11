package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = RubyRed,
    onPrimary = Color.White,
    primaryContainer = RubyRedDark,
    onPrimaryContainer = Color.White,
    secondary = KalashBlue,
    onSecondary = Color.White,
    secondaryContainer = KalashBlueDark,
    onSecondaryContainer = Color.White,
    tertiary = CyanAccent,
    background = DeepNavy,
    onBackground = TextPrimary,
    surface = SurfaceNavy,
    onSurface = TextPrimary,
    surfaceVariant = CardNavy,
    onSurfaceVariant = TextSecondary,
    outline = BorderSubtle
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Ishpata Films uses a unified cinematic dark theme representing theatrical and cultural cinema
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
