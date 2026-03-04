package com.paoapps.kombutime

import com.russhwolf.settings.Settings

/**
 * Platform-specific settings factory
 */
expect object SettingsFactory {
    fun createSettings(): Settings
}
