package com.paoapps.kombutime

import com.russhwolf.settings.Settings

/**
 * Android settings factory - uses default Settings
 */
actual object SettingsFactory {
    actual fun createSettings(): Settings {
        val settings = Settings()

        // Migrate old name-based brews to number-based if needed
        migrateNameToNumberIfNeeded(settings)

        return settings
    }

    private fun migrateNameToNumberIfNeeded(settings: Settings) {
        val migrationKey = "name_to_number_migrated"
        if (settings.getBoolean(migrationKey, false)) {
            // Already migrated
            return
        }

        val brewsJson = settings.getString("brews", "[]")
        if (brewsJson.contains("\"name\"")) {
            // Old format with name strings, convert to numbers
            val converted = convertNamesToNumbers(brewsJson)
            println("Android Migration: Converting names to numbers")
            println("Original: $brewsJson")
            println("Converted: $converted")
            settings.putString("brews", converted)
        }

        // Mark migration as complete
        settings.putBoolean(migrationKey, true)
    }

    /**
     * Converts old name-based brew JSON to number-based format
     * Extracts numbers from names like "Batch 1", "Brew 2", "Brouwsel 3", "Ansatz 4"
     */
    private fun convertNamesToNumbers(json: String): String {
        // Pattern to match "name":"Something X" where X is a number
        val namePattern = """"name"\s*:\s*"([^"]*\s+)?(\d+)"""".toRegex()

        return namePattern.replace(json) { matchResult ->
            val number = matchResult.groupValues[2]
            """"nameNumber":$number"""
        }
    }
}
