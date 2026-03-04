package com.paoapps.kombutime

import platform.Foundation.NSUserDefaults
import platform.Foundation.NSDate

/**
 * iOS implementation of widget updater
 * Writes a timestamp to UserDefaults that the widget can monitor
 */
actual object WidgetUpdater {
    actual fun updateWidgets() {
        // Write a timestamp to signal the widget should update
        // The widget will check this on its next refresh
        val defaults = NSUserDefaults(suiteName = "group.com.paoapps.kombutime")
        defaults?.setObject(NSDate(), forKey = "lastUpdate")
        defaults?.synchronize()
        println("Updated widget refresh timestamp")
    }
}
