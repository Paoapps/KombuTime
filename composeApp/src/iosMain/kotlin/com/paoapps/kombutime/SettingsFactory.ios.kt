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
        if (appGroupDefaults.boolForKey(migrationKey)) {
            println("Data already migrated to App Group")
            return
        }
        
        println("Checking for data to migrate from standard UserDefaults...")
        
        // List of keys to migrate
        val keysToMigrate = listOf("brews", "notificationTime", "flavors")
        var migratedAny = false
        
        for (key in keysToMigrate) {
            val value = standardDefaults.objectForKey(key)
            if (value != null) {
                println("Migrating key '$key' to App Group")
                appGroupDefaults.setObject(value, key)
                migratedAny = true
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
