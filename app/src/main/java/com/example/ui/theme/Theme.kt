package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val TarunDarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = SpaceDarkBackground,
    primaryContainer = DeepSapphire,
    onPrimaryContainer = TextPrimary,
    secondary = HologramTeal,
    onSecondary = SpaceDarkBackground,
    secondaryContainer = GlassSurfaceVariant,
    onSecondaryContainer = HologramTeal,
    tertiary = CorePulseViolet,
    onTertiary = Color.White,
    background = SpaceDarkBackground,
    onBackground = TextPrimary,
    surface = GlassSurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = GlassSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = GlassBorderActive,
    outlineVariant = GlassBorder,
    error = AlertRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Futuristic dark theme by default
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = TarunDarkColorScheme,
        typography = Typography,
        content = content
    )
}

