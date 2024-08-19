package com.paoapps.kombutime.domain

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import kotlinx.serialization.Serializable

@Serializable
data class Batch(
    val startDate: LocalDate,
    val settings: BatchSettings,
    val state: BatchState = BatchState.FirstFermentation,
) {
    constructor(startDate: LocalDate, name: String): this(startDate, BatchSettings(name = name))

    val endDate: LocalDate
        get() = when (state) {
            is BatchState.FirstFermentation -> startDate.plus(settings.firstFermentationDays, DateTimeUnit.DAY)
            is BatchState.SecondFermentation -> startDate.plus(settings.secondFermentationDays, DateTimeUnit.DAY)
        }
}

@Serializable
sealed class BatchState {
    @Serializable
    data object FirstFermentation : BatchState()
    @Serializable
    data class SecondFermentation(
        val flavor: String,
    ) : BatchState()
}

@Serializable
data class BatchSettings(
    val firstFermentationDays: Int = 12,
    val secondFermentationDays: Int = 3,
    val name: String
)
