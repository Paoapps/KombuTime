package com.paoapps.kombutime.widget

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Helper object to update widgets when brew data changes
 */
object BrewWidgetUpdater {

    fun updateWidgets(context: Context) {
        Log.d("BrewWidgetUpdater", "updateWidgets() called")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("BrewWidgetUpdater", "Calling updateAll() on BrewWidget")
                BrewWidget().updateAll(context)
                Log.d("BrewWidgetUpdater", "Widget update completed successfully")
            } catch (e: Exception) {
                // Widget might not be added to home screen yet
                Log.e("BrewWidgetUpdater", "Failed to update widget", e)
                e.printStackTrace()
            }
        }
    }
}
