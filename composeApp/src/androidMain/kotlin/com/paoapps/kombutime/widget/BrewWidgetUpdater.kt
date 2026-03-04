package com.paoapps.kombutime.widget

import android.content.Context
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
        CoroutineScope(Dispatchers.IO).launch {
            try {
                BrewWidget().updateAll(context)
            } catch (e: Exception) {
                // Widget might not be added to home screen yet
                e.printStackTrace()
            }
        }
    }
}
