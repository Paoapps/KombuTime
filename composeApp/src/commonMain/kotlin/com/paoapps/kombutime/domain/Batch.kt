package com.paoapps.kombutime.domain

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class Batch(
    val startDate: LocalDate,
    val settings: BatchSettings,
    val state: BatchState = BatchState.FirstFermentation,
) {
    constructor(startDate: LocalDate, name: String): this(startDate, BatchSettings(name = name))
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
