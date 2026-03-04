package com.paoapps.kombutime

import com.paoapps.kombutime.widget.BrewWidgetUpdater

/**
 * Android implementation of widget updater
 */
actual object WidgetUpdater {
    private var appContext: android.content.Context? = null

    fun init(context: android.content.Context) {
        appContext = context.applicationContext
    }

    actual fun updateWidgets() {
        appContext?.let { context ->
            BrewWidgetUpdater.updateWidgets(context)
        }
    }
}
