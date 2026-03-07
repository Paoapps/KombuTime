package com.paoapps.kombutime.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paoapps.kombutime.domain.HistoricalBrew
import com.paoapps.kombutime.model.HistoryRepository
import com.paoapps.kombutime.model.HistoryStatistics
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class HistoryViewModel : ViewModel(), KoinComponent {

    private val historyRepository: HistoryRepository by inject()

    val output: StateFlow<Output> = historyRepository.historicalBrews.map { brews ->
        val statistics = historyRepository.getStatistics()

        Output(
            historicalBrews = brews.map { brew ->
                HistoricalBrewItem(
                    id = brew.id,
                    name = "Brew ${brew.nameNumber}",
                    teaType = brew.teaType,
                    flavor = brew.flavor,
                    completedDate = brew.completedDate,
                    startDate = brew.startDate,
                    bottledDate = brew.bottledDate,
                    firstFermentationDays = brew.firstFermentationDays,
                    secondFermentationDays = brew.secondFermentationDays,
                    totalDays = brew.firstFermentationDays + brew.secondFermentationDays
                )
            },
            statistics = statistics,
            isEmpty = brews.isEmpty()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Output(
            historicalBrews = emptyList(),
            statistics = HistoryStatistics(0, null, null, 0.0, 0.0),
            isEmpty = true
        )
    )

    fun exportCSV(): String {
        return historyRepository.exportAsCSV()
    }

    fun exportJSON(): String {
        return historyRepository.exportAsJSON()
    }

    fun clearHistory() {
        historyRepository.clearHistory()
    }

    data class Output(
        val historicalBrews: List<HistoricalBrewItem>,
        val statistics: HistoryStatistics,
        val isEmpty: Boolean
    )

    data class HistoricalBrewItem(
        val id: String,
        val name: String,
        val teaType: String,
        val flavor: String,
        val completedDate: kotlinx.datetime.LocalDate,
        val startDate: kotlinx.datetime.LocalDate,
        val bottledDate: kotlinx.datetime.LocalDate,
        val firstFermentationDays: Int,
        val secondFermentationDays: Int,
        val totalDays: Int
    )
}
