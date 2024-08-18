package com.paoapps.kombutime.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import java.text.SimpleDateFormat
import java.util.Locale

actual fun formatDate(
    date: LocalDate,
    format: LocalDateFormat
): String {
    val locale = Locale.getDefault()
    val millis = date.epochMilliseconds

    val patters = when (format) {
        LocalDateFormat.LONG -> "EEEE, MMMM d, yyyy"
    }

    return SimpleDateFormat(patters, locale).format(millis)
}
