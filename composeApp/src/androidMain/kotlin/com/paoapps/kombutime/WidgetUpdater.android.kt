package com.paoapps.kombutime

import android.util.Log
import com.paoapps.kombutime.widget.BrewWidgetUpdater

/**
 * Android implementation of widget updater
 */
actual object WidgetUpdater {
    private var appContext: android.content.Context? = null

    fun init(context: android.content.Context) {
        appContext = context.applicationContext
        Log.d("WidgetUpdater", "Initialized with context: $appContext")
    }

    actual fun updateWidgets() {
        Log.d("WidgetUpdater", "updateWidgets() called, appContext: $appContext")
        appContext?.let { context ->
            BrewWidgetUpdater.updateWidgets(context)
        } ?: Log.e("WidgetUpdater", "Cannot update widgets: appContext is null!")
    }
}
