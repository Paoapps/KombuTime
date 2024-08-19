package com.paoapps.kombutime.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaLocalDateTime
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Locale

actual fun formatDate(
    date: LocalDate,
    format: LocalDateFormat
): String {
    val locale = Locale.getDefault()
    val millis = date.epochMilliseconds

    val patters = when (format) {
        LocalDateFormat.LONG -> "EEEE, MMMM d, yyyy"
        LocalDateFormat.SHORT -> "MMM d, yyyy"
    }

    return SimpleDateFormat(patters, locale).format(millis)
}

actual fun formatTime(time: LocalTime): String {
    val format = DateTimeFormatter.ofPattern("hh:mm a")
    return LocalDateTime(2000, 1, 1, time.hour, time.minute).toJavaLocalDateTime().format(format)
}
