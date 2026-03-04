package com.paoapps.kombutime

import com.russhwolf.settings.Settings

/**
 * Android settings factory - uses default Settings
 */
actual object SettingsFactory {
    actual fun createSettings(): Settings = Settings()
}
