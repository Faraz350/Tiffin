package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkAestheticColorScheme = darkColorScheme(
    primary = FarazCyan,
    onPrimary = Color(0xFF062438),
    primaryContainer = Color(0xFF163C52),
    onPrimaryContainer = Color(0xFFBAE6FD),
    secondary = FarazAmber,
    onSecondary = Color(0xFF3B2500),
    secondaryContainer = Color(0xFF452B00),
    onSecondaryContainer = Color(0xFFFDE68A),
    tertiary = PresentGreenNeon,
    onTertiary = Color(0xFF003822),
    tertiaryContainer = PresentGreenDarkBg,
    onTertiaryContainer = Color(0xFFA7F3D0),
    background = DarkAppBackground,
    onBackground = DarkTextPrimary,
    surface = DarkCardSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkCardSurfaceElevated,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkCardBorder,
    outlineVariant = Color(0xFF222938),
    error = AbsentRedNeon,
    onError = Color(0xFF450A0A)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force / default dark aesthetic as requested by user
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkAestheticColorScheme,
        typography = Typography,
        content = content
    )
}
