package com.paoapps.kombutime

/**
 * iOS implementation of widget updater
 * Note: Widget updates are handled automatically by the widget's timeline provider
 * which polls the shared UserDefaults data
 */
actual object WidgetUpdater {
    actual fun updateWidgets() {
        // On iOS, widgets automatically update based on their timeline
        // The timeline provider reads from App Groups UserDefaults
        // No manual refresh needed as the widget will poll the data
    }
}
