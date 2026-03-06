package com.paoapps.kombutime.widget

import android.content.Context
import android.util.Log
import com.paoapps.kombutime.SettingsFactory
import com.paoapps.kombutime.domain.Brew
import com.paoapps.kombutime.domain.BrewState
import com.russhwolf.settings.Settings
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

/**
 * Provides brew data to the widget
 */
object BrewWidgetDataProvider {

    private val jsonParser = Json

    /**
     * Get brews from storage, sorted by days remaining (most urgent first)
     */
    fun getBrews(context: Context): List<Brew> {
        return try {
            val storage = SettingsFactory.createSettings()
            val brewsJson = storage.getString("brews", "[]")
            Log.d("BrewWidget", "Brews JSON from storage: $brewsJson")

            val brews = jsonParser.decodeFromString(ListSerializer(Brew.serializer()), brewsJson)
            Log.d("BrewWidget", "Successfully parsed ${brews.size} brews")

            // Sort by days remaining (ascending) - most urgent brews first
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            brews.sortedBy { brew ->
                val fermentationDays = when(brew.state) {
                    is BrewState.FirstFermentation -> brew.settings.firstFermentationDays
                    is BrewState.SecondFermentation -> brew.settings.secondFermentationDays
                }
                fermentationDays - (today - brew.startDate).days
            }
        } catch (e: Exception) {
            Log.e("BrewWidget", "Error loading brews", e)
            emptyList()
        }
    }
}
