package com.paoapps.kombutime.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object DesignSystem {

    // iOS-inspired Colors with Dark Mode Support
    object Colors {
        // Light Mode Colors
        object Light {
            val backgroundPrimary = Color(0xFFF2F2F7)
            val backgroundSecondary = Color(0xFFFFFFFF)
            val backgroundTertiary = Color(0xFFF9F9FB)
            val cardBackground = Color(0xFFFFFFFF)
            val cardBackgroundElevated = Color(0xFFFFFFFF)
            val tabBarBackground = Color(0xFFFFFFFF)
            val tabBarUnselected = Color(0xFF8E8E93)
            val progressBackground = Color(0xFFE5E5EA)
            val textPrimary = Color(0xFF000000)
            val textSecondary = Color(0xFF3C3C43).copy(alpha = 0.6f)
            val textTertiary = Color(0xFF3C3C43).copy(alpha = 0.3f)
            val divider = Color(0xFF3C3C43).copy(alpha = 0.15f)
        }

        // Dark Mode Colors
        object Dark {
            val backgroundPrimary = Color(0xFF000000)
            val backgroundSecondary = Color(0xFF1C1C1E)
            val backgroundTertiary = Color(0xFF2C2C2E)
            val cardBackground = Color(0xFF1C1C1E)
            val cardBackgroundElevated = Color(0xFF2C2C2E)
            val tabBarBackground = Color(0xFF1C1C1E).copy(alpha = 0.95f)
            val tabBarUnselected = Color(0xFF8E8E93)
            val progressBackground = Color(0xFF3A3A3C)
            val textPrimary = Color(0xFFFFFFFF)
            val textSecondary = Color(0xFFEBEBF5).copy(alpha = 0.6f)
            val textTertiary = Color(0xFFEBEBF5).copy(alpha = 0.3f)
            val divider = Color(0xFFEBEBF5).copy(alpha = 0.15f)
        }

        // Adaptive Colors
        val backgroundPrimary: Color @Composable get() = if (isSystemInDarkTheme()) Dark.backgroundPrimary else Light.backgroundPrimary
        val backgroundSecondary: Color @Composable get() = if (isSystemInDarkTheme()) Dark.backgroundSecondary else Light.backgroundSecondary
        val backgroundTertiary: Color @Composable get() = if (isSystemInDarkTheme()) Dark.backgroundTertiary else Light.backgroundTertiary
        val cardBackground: Color @Composable get() = if (isSystemInDarkTheme()) Dark.cardBackground else Light.cardBackground
        val cardBackgroundElevated: Color @Composable get() = if (isSystemInDarkTheme()) Dark.cardBackgroundElevated else Light.cardBackgroundElevated
        val tabBarBackground: Color @Composable get() = if (isSystemInDarkTheme()) Dark.tabBarBackground else Light.tabBarBackground
        val tabBarUnselected: Color @Composable get() = if (isSystemInDarkTheme()) Dark.tabBarUnselected else Light.tabBarUnselected
        val progressBackground: Color @Composable get() = if (isSystemInDarkTheme()) Dark.progressBackground else Light.progressBackground
        val textPrimary: Color @Composable get() = if (isSystemInDarkTheme()) Dark.textPrimary else Light.textPrimary
        val textSecondary: Color @Composable get() = if (isSystemInDarkTheme()) Dark.textSecondary else Light.textSecondary
        val textTertiary: Color @Composable get() = if (isSystemInDarkTheme()) Dark.textTertiary else Light.textTertiary
        val divider: Color @Composable get() = if (isSystemInDarkTheme()) Dark.divider else Light.divider

        // Colors that stay the same in both modes
        val tabBarSelected = Color(0xFF007AFF)
        val progressActive = Color(0xFF34C759)
        val progressWarning = Color(0xFFFF9500)
        val progressOverdue = Color(0xFFFF3B30)
        val accentBlue = Color(0xFF007AFF)
        val accentGreen = Color(0xFF34C759)
        val accentOrange = Color(0xFFFF9500)
        val accentRed = Color(0xFFFF3B30)
        val accentPurple = Color(0xFFAF52DE)

        // Overlays
        val overlayLight = Color(0xFFFFFFFF).copy(alpha = 0.8f)
        val overlayDark = Color(0xFF000000).copy(alpha = 0.4f)
    }

    // Spacing
    object Spacing {
        val extraSmall = 4.dp
        val small = 8.dp
        val medium = 12.dp
        val large = 16.dp
        val extraLarge = 24.dp
        val huge = 32.dp

        val screenPadding = 16.dp
        val cardPadding = 16.dp
        val sectionSpacing = 24.dp
    }

    // Corner Radius
    object CornerRadius {
        val small = RoundedCornerShape(8.dp)
        val medium = RoundedCornerShape(12.dp)
        val large = RoundedCornerShape(16.dp)
        val extraLarge = RoundedCornerShape(20.dp)
        val pill = RoundedCornerShape(100.dp)
    }

    // Elevation
    object Elevation {
        val none = 0.dp
        val small = 2.dp
        val medium = 4.dp
        val large = 8.dp
        val extraLarge = 16.dp
    }
}
