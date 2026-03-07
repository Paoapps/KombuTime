package com.paoapps.kombutime.domain

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

/**
 * Represents a completed kombucha brew saved to history.
 * Created when a brew completes second fermentation.
 */
@Serializable
data class HistoricalBrew(
    val id: String,
    val nameNumber: Int,
    val teaType: String = "",
    val flavor: String = "",
    val startDate: LocalDate,
    val bottledDate: LocalDate,  // When first fermentation completed
    val completedDate: LocalDate,  // When second fermentation completed
    val firstFermentationDays: Int,  // Actual days used for F1
    val secondFermentationDays: Int,  // Actual days used for F2
)
