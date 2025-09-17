package com.hassanjamil.hqibla.sample.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = PrimaryRed,
    onPrimary = Color.White,
    primaryContainer = PrimaryRed,
    onPrimaryContainer = Color.White,
    secondary = AccentGold,
    onSecondary = Color.Black,
    secondaryContainer = AccentGold.copy(alpha = 0.85f),
    onSecondaryContainer = Color.Black,
    background = NeutralBackground,
    onBackground = Color(0xFF1C1B1F),
    surface = NeutralBackground,
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = NeutralBackground.copy(alpha = 0.95f),
    onSurfaceVariant = Color(0xFF49454F),
    outlineVariant = Color(0x33000000)
)

private val DarkColors = darkColorScheme(
    primary = PrimaryRed,
    onPrimary = Color.White,
    primaryContainer = PrimaryRed,
    onPrimaryContainer = Color.White,
    secondary = AccentGold,
    onSecondary = Color.Black,
    secondaryContainer = AccentGold.copy(alpha = 0.7f),
    onSecondaryContainer = Color.Black,
    background = Color(0xFF1C1B1F),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF2D3138),
    onSurfaceVariant = Color(0xFFCAC4D0),
    outlineVariant = Color(0x66FFFFFF)
)

@Composable
fun HjQiblaCompassTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
