package com.paoapps.kombutime.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

enum class LocalDateFormat {
    SHORT,
    LONG,
}

expect fun formatDate(date: LocalDate, format: LocalDateFormat): String

val LocalDate.epochMilliseconds get() = LocalDateTime(year, monthNumber, dayOfMonth, 0, 0, 0, 0).toInstant(
    TimeZone.currentSystemDefault()).toEpochMilliseconds()

val LocalDate.epochSeconds get() = LocalDateTime(year, monthNumber, dayOfMonth, 0, 0, 0, 0).toInstant(
    TimeZone.currentSystemDefault()).epochSeconds
