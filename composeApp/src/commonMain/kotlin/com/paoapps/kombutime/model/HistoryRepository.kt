@file:OptIn(kotlin.time.ExperimentalTime::class)

package com.paoapps.kombutime.model

import com.paoapps.kombutime.SettingsFactory
import com.paoapps.kombutime.domain.Brew
import com.paoapps.kombutime.domain.BrewState
import com.paoapps.kombutime.domain.HistoricalBrew
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlin.random.Random
import kotlin.time.Clock

/**
 * Repository for managing historical brew data.
 * Stores completed brews using JSON in platform Settings.
 */
class HistoryRepository {
    
    private val jsonParser = Json { prettyPrint = true }
    private val storage: Settings = SettingsFactory.createSettings()
    
    private val _historicalBrews = MutableStateFlow(loadHistory())
    val historicalBrews: Flow<List<HistoricalBrew>> = _historicalBrews
    
    private val _saveToHistory = MutableStateFlow(storage["saveToHistory", true])
    val saveToHistory: Flow<Boolean> = _saveToHistory
    
    /**
     * Load history from storage
     */
    private fun loadHistory(): List<HistoricalBrew> {
        val historyJson = storage["history", "[]"]
        return try {
            jsonParser.decodeFromString(ListSerializer(HistoricalBrew.serializer()), historyJson)
        } catch (e: Exception) {
            println("Failed to load history: ${e.message}")
            emptyList()
        }
    }
    
    /**
     * Save a completed brew to history
     */
    fun saveCompletedBrew(brew: Brew) {
        if (!_saveToHistory.value) return
        
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        
        // Calculate the bottled date (when F1 ended)
        // If currently in F2, the bottled date was when F2 started
        val bottledDate = if (brew.state is BrewState.SecondFermentation) {
            brew.startDate
        } else {
            // If in F1, it's being completed now
            now
        }
        
        // Calculate original F1 start date
        val originalStartDate = if (brew.state is BrewState.SecondFermentation) {
            // F2 started at brew.startDate, so F1 started firstFermentationDays before that
            brew.startDate.plus(-brew.settings.firstFermentationDays, DateTimeUnit.DAY)
        } else {
            brew.startDate
        }
        
        val teaType = when (brew.state) {
            is BrewState.FirstFermentation -> brew.state.teaType
            is BrewState.SecondFermentation -> ""  // Tea type is only tracked in F1
        }
        
        val flavor = when (brew.state) {
            is BrewState.FirstFermentation -> ""
            is BrewState.SecondFermentation -> brew.state.flavor
        }
        
        val historicalBrew = HistoricalBrew(
            id = Random.nextLong().toString(),
            nameNumber = brew.settings.nameNumber,
            teaType = teaType,
            flavor = flavor,
            startDate = originalStartDate,
            bottledDate = bottledDate,
            completedDate = now,
            firstFermentationDays = brew.settings.firstFermentationDays,
            secondFermentationDays = brew.settings.secondFermentationDays
        )
        
        _historicalBrews.value = (_historicalBrews.value + historicalBrew)
            .sortedByDescending { it.completedDate }  // Most recent first
        
        saveHistory()
    }
    
    /**
     * Clear all history
     */
    fun clearHistory() {
        _historicalBrews.value = emptyList()
        saveHistory()
    }
    
    /**
     * Delete a specific historical brew
     */
    fun deleteHistoricalBrew(id: String) {
        _historicalBrews.value = _historicalBrews.value.filter { it.id != id }
        saveHistory()
    }
    
    /**
     * Toggle saving to history
     */
    fun setSaveToHistory(enabled: Boolean) {
        _saveToHistory.value = enabled
        storage["saveToHistory"] = enabled
    }
    
    /**
     * Export history as CSV
     */
    fun exportAsCSV(): String {
        val header = "Name,Tea Type,Flavor,Start Date,Bottled Date,Completed Date,F1 Days,F2 Days\n"
        val rows = _historicalBrews.value.joinToString("\n") { brew ->
            val name = "Brew ${brew.nameNumber}"
            val teaType = brew.teaType.ifEmpty { "-" }
            val flavor = brew.flavor.ifEmpty { "-" }
            "$name,$teaType,$flavor,${brew.startDate},${brew.bottledDate},${brew.completedDate},${brew.firstFermentationDays},${brew.secondFermentationDays}"
        }
        return header + rows
    }
    
    /**
     * Export history as JSON
     */
    fun exportAsJSON(): String {
        return jsonParser.encodeToString(
            ListSerializer(HistoricalBrew.serializer()), 
            _historicalBrews.value
        )
    }
    
    /**
     * Get statistics from history
     */
    fun getStatistics(): HistoryStatistics {
        val brews = _historicalBrews.value
        
        if (brews.isEmpty()) {
            return HistoryStatistics(
                totalBrews = 0,
                firstBrewDate = null,
                mostUsedFlavor = null,
                averageF1Days = 0.0,
                averageF2Days = 0.0
            )
        }
        
        val firstBrew = brews.minByOrNull { it.startDate }
        val flavorsWithCount = brews
            .filter { it.flavor.isNotEmpty() }
            .groupingBy { it.flavor }
            .eachCount()
        val mostUsedFlavor = flavorsWithCount.maxByOrNull { it.value }
        
        return HistoryStatistics(
            totalBrews = brews.size,
            firstBrewDate = firstBrew?.startDate,
            mostUsedFlavor = mostUsedFlavor?.let { "${it.key} (${it.value}x)" },
            averageF1Days = brews.map { it.firstFermentationDays }.average(),
            averageF2Days = brews.map { it.secondFermentationDays }.average()
        )
    }
    
    /**
     * Save history to storage
     */
    private fun saveHistory() {
        storage["history"] = jsonParser.encodeToString(
            ListSerializer(HistoricalBrew.serializer()), 
            _historicalBrews.value
        )
    }
}

/**
 * Statistics calculated from history
 */
data class HistoryStatistics(
    val totalBrews: Int,
    val firstBrewDate: kotlinx.datetime.LocalDate?,
    val mostUsedFlavor: String?,
    val averageF1Days: Double,
    val averageF2Days: Double
)
