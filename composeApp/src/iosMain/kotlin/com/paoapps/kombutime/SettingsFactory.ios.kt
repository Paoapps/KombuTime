package com.paoapps.kombutime

import com.russhwolf.settings.Settings
import com.russhwolf.settings.NSUserDefaultsSettings
import platform.Foundation.NSUserDefaults

/**
 * iOS settings factory - uses App Group for widget data sharing
 */
actual object SettingsFactory {
    private const val APP_GROUP = "group.com.paoapps.kombutime"

    actual fun createSettings(): Settings {
        val userDefaults = NSUserDefaults(suiteName = APP_GROUP)
        if (userDefaults == null) {
            println("ERROR: Failed to create NSUserDefaults with suite name: $APP_GROUP")
            println("Falling back to standard UserDefaults - widgets will not work!")
            return NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults)
        }
        println("SUCCESS: Created NSUserDefaults with App Group: $APP_GROUP")

        // Migrate data from standard UserDefaults to App Group if needed
        migrateDataIfNeeded(userDefaults)

        return NSUserDefaultsSettings(userDefaults)
    }

    private fun migrateDataIfNeeded(appGroupDefaults: NSUserDefaults) {
        val standardDefaults = NSUserDefaults.standardUserDefaults

        // Check if we've already migrated
        val migrationKey = "data_migrated_to_app_group"
        val alreadyMigrated = appGroupDefaults.boolForKey(migrationKey)

        // Also check if brews data exists and is in the correct format
        val brewsData = appGroupDefaults.objectForKey("brews")
        val hasBrewsInAppGroup = brewsData != null
        val brewsDataString = brewsData?.toString() ?: ""
        val needsFormatConversion = brewsDataString.contains("BatchState") || brewsDataString.contains("BatchSettings")

        if (alreadyMigrated && hasBrewsInAppGroup && !needsFormatConversion) {
            println("Data already migrated to App Group")
            return
        }

        if (alreadyMigrated && !hasBrewsInAppGroup) {
            println("Migration flag set but brews data missing - re-running migration")
        }
        
        if (alreadyMigrated && needsFormatConversion) {
            println("Migration flag set but brews data needs format conversion - re-running migration")
        }

        println("Checking for data to migrate from standard UserDefaults...")

        // Debug: Print all keys in standard UserDefaults
        val allKeys = standardDefaults.dictionaryRepresentation().keys
        println("All keys in standard UserDefaults: $allKeys")

        // List of keys to migrate (including old "batches" key from before rename)
        val keysToMigrate = listOf("brews", "batches", "notificationTime", "savedFlavors", "promptForFlavor")
        var migratedAny = false

        for (key in keysToMigrate) {
            val value = standardDefaults.objectForKey(key)
            if (value != null) {
                // Special case: migrate "batches" to "brews" and convert Batch class names to Brew
                if (key == "batches") {
                    val batchesJson = value.toString()
                    // Convert Batch class names to Brew class names in the JSON
                    val brewsJson = batchesJson
                        .replace("BatchState", "BrewState")
                        .replace("BatchSettings", "BrewSettings")
                    println("Migrating key '$key' to App Group as 'brews' (converted Batch->Brew)")
                    println("Original: $batchesJson")
                    println("Converted: $brewsJson")
                    appGroupDefaults.setObject(brewsJson, "brews")
                    migratedAny = true
                } else {
                    println("Migrating key '$key' to App Group as '$key' (value: $value)")
                    appGroupDefaults.setObject(value, key)
                    migratedAny = true
                }
            } else {
                println("Key '$key' not found in standard UserDefaults")
            }
        }

        if (migratedAny) {
            println("Successfully migrated data to App Group")
        } else {
            println("No data found to migrate")
        }

        // Mark migration as complete
        appGroupDefaults.setBool(true, migrationKey)
        appGroupDefaults.synchronize()

        println("Migration complete")
    }
}
