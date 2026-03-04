package com.paoapps.kombutime.domain

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import kotlinx.serialization.Serializable

@Serializable
data class Brew(
    val startDate: LocalDate,
    val settings: BrewSettings,
    val state: BrewState = BrewState.FirstFermentation(),
) {
    constructor(startDate: LocalDate, name: String): this(startDate, BrewSettings(name = name))

    val endDate: LocalDate
        get() = when (state) {
            is BrewState.FirstFermentation -> startDate.plus(settings.firstFermentationDays, DateTimeUnit.DAY)
            is BrewState.SecondFermentation -> startDate.plus(settings.secondFermentationDays, DateTimeUnit.DAY)
        }
}

@Serializable
sealed class BrewState {
    @Serializable
    data class FirstFermentation(
        val teaType: String = "",
    ) : BrewState()
    @Serializable
    data class SecondFermentation(
        val flavor: String = "",
    ) : BrewState()
}

@Serializable
data class BrewSettings(
    val firstFermentationDays: Int = 12,
    val secondFermentationDays: Int = 3,
    val name: String
)
