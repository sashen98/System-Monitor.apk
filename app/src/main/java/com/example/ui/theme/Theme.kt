package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val SystemMonitorDarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = CyberDarkBg,
    primaryContainer = CyberCardBg,
    onPrimaryContainer = NeonCyanLight,
    secondary = NeonGreen,
    onSecondary = CyberDarkBg,
    secondaryContainer = CyberCardBorder,
    onSecondaryContainer = TextPrimary,
    tertiary = NeonPurple,
    background = CyberDarkBg,
    onBackground = TextPrimary,
    surface = CyberCardBg,
    onSurface = TextPrimary,
    surfaceVariant = CyberCardBorder,
    onSurfaceVariant = TextSecondary,
    outline = CyberCardBorder
)

@Composable
fun SystemMonitorTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = SystemMonitorDarkColorScheme,
        typography = Typography,
        content = content
    )
}
