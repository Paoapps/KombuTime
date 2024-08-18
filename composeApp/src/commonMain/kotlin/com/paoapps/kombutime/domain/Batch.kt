package com.paoapps.kombutime.domain

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class Batch(
    val startDate: LocalDate
)
