package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Clean, modern dark surfaces
val DarkBackground = Color(0xFF0F131D)
val DarkSurface = Color(0xFF181F2E)
val DarkSurfaceVariant = Color(0xFF232D42)
val DarkCardBorder = Color(0xFF2C3952)

// Text tokens
val TextPrimary = Color(0xFFF8FAFC)
val TextSecondary = Color(0xFF94A3B8)
val TextMuted = Color(0xFF64748B)

val ErrorColor = Color(0xFFF43F5E)

enum class GradientTheme(
    val title: String,
    val primaryColor: Color,
    val secondaryColor: Color
) {
    CYAN_NEON(
        title = "Неоновый Циан",
        primaryColor = Color(0xFF00F0FF),
        secondaryColor = Color(0xFF0284C7)
    ),
    PURPLE_VIBE(
        title = "Электро Пурпур",
        primaryColor = Color(0xFFA855F7),
        secondaryColor = Color(0xFF6366F1)
    ),
    SUNSET_EMBER(
        title = "Закатный Огонь",
        primaryColor = Color(0xFFF43F5E),
        secondaryColor = Color(0xFFF59E0B)
    ),
    EMERALD_MINT(
        title = "Изумрудная Мята",
        primaryColor = Color(0xFF10B981),
        secondaryColor = Color(0xFF059669)
    ),
    ELECTRIC_PINK(
        title = "Розовый Неон",
        primaryColor = Color(0xFFEC4899),
        secondaryColor = Color(0xFF8B5CF6)
    );

    val brush: Brush
        get() = Brush.horizontalGradient(listOf(primaryColor, secondaryColor))
}
