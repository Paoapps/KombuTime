package com.paoapps.kombutime.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = PRIMARY_COLOR,
    background = DesignSystem.Colors.Light.backgroundPrimary,
    surface = DesignSystem.Colors.Light.cardBackground,
    onPrimary = DesignSystem.Colors.Light.textPrimary,
    onBackground = DesignSystem.Colors.Light.textPrimary,
    onSurface = DesignSystem.Colors.Light.textPrimary,
)

private val DarkColorScheme = darkColorScheme(
    primary = PRIMARY_COLOR,
    background = DesignSystem.Colors.Dark.backgroundPrimary,
    surface = DesignSystem.Colors.Dark.cardBackground,
    onPrimary = DesignSystem.Colors.Dark.textPrimary,
    onBackground = DesignSystem.Colors.Dark.textPrimary,
    onSurface = DesignSystem.Colors.Dark.textPrimary,
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
