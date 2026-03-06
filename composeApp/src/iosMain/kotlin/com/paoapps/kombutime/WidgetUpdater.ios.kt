package com.paoapps.kombutime

import platform.Foundation.NSUserDefaults
import platform.Foundation.NSDate

/**
 * iOS implementation of widget updater
 * Writes a timestamp and triggers widget reload via notification
 */
actual object WidgetUpdater {
    actual fun updateWidgets() {
        // Write a timestamp to signal the widget should update
        val defaults = NSUserDefaults(suiteName = "group.com.paoapps.kombutime")
        defaults?.setObject(NSDate(), forKey = "lastUpdate")
        defaults?.synchronize()

        // Post notification that Swift code can observe to trigger widget reload
        platform.Foundation.NSNotificationCenter.defaultCenter.postNotificationName(
            "BrewDataChanged",
            `object` = null
        )
        println("Posted BrewDataChanged notification for widget update")
    }
}
