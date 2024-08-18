package com.paoapps.kombutime.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeFormatBuilder
import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSDateFormatterFullStyle
import platform.Foundation.NSDateFormatterLongStyle
import platform.Foundation.NSTimeZone
import platform.Foundation.timeZoneWithAbbreviation

actual fun formatDate(
    date: LocalDate,
    format: LocalDateFormat
): String {
    val epochSeconds = date.epochSeconds
    // Apple uses 00:00:00 UTC on 1 January 2001 as the reference date
    val secondsSince2001 = epochSeconds - 978307200
    val nsDate = NSDate(secondsSince2001.toDouble())

    val formatter = NSDateFormatter()
    formatter.dateStyle = when (format) {
        LocalDateFormat.LONG -> NSDateFormatterFullStyle
    }

    return formatter.stringFromDate(nsDate)
}
