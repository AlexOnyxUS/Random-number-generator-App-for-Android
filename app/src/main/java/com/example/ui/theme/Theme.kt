package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf

val LocalActiveGradient = compositionLocalOf { GradientTheme.CYAN_NEON }

@Composable
fun MyApplicationTheme(
    activeGradient: GradientTheme = GradientTheme.CYAN_NEON,
    content: @Composable () -> Unit
) {
    val colorScheme = darkColorScheme(
        primary = activeGradient.primaryColor,
        secondary = activeGradient.secondaryColor,
        background = DarkBackground,
        surface = DarkSurface,
        surfaceVariant = DarkSurfaceVariant,
        onBackground = TextPrimary,
        onSurface = TextPrimary,
        outline = DarkCardBorder,
        error = ErrorColor
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
