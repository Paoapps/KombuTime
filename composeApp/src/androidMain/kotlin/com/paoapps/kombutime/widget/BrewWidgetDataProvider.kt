package com.paoapps.kombutime.widget

import android.content.Context
import android.util.Log
import com.paoapps.kombutime.SettingsFactory
import com.paoapps.kombutime.domain.Brew
import com.russhwolf.settings.Settings
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

/**
 * Provides brew data to the widget
 */
object BrewWidgetDataProvider {

    private val jsonParser = Json

    /**
     * Get brews from storage
     */
    fun getBrews(context: Context): List<Brew> {
        return try {
            val storage = SettingsFactory.createSettings()
            val brewsJson = storage.getString("brews", "[]")
            Log.d("BrewWidget", "Brews JSON from storage: $brewsJson")

            val brews = jsonParser.decodeFromString(ListSerializer(Brew.serializer()), brewsJson)
            Log.d("BrewWidget", "Successfully parsed ${brews.size} brews")
            brews
        } catch (e: Exception) {
            Log.e("BrewWidget", "Error loading brews", e)
            emptyList()
        }
    }
}
