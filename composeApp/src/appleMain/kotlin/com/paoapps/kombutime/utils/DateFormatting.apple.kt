package com.paoapps.kombutime.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitDay
import platform.Foundation.NSCalendarUnitMonth
import platform.Foundation.NSCalendarUnitYear
import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSDateFormatterFullStyle
import platform.Foundation.NSDateFormatterShortStyle

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
        LocalDateFormat.SHORT -> NSDateFormatterShortStyle
    }

    return formatter.stringFromDate(nsDate)
}

actual fun formatTime(time: LocalTime): String {
    val calendar = NSCalendar.currentCalendar
    val components = calendar.components(
        NSCalendarUnitYear or NSCalendarUnitMonth or NSCalendarUnitDay,
        NSDate()
    )
    components.setHour(time.hour.toLong())
    components.setMinute(time.minute.toLong())
    components.setSecond(0)

    val formatter = NSDateFormatter()
    formatter.timeStyle = NSDateFormatterShortStyle

    val nsDate = calendar.dateFromComponents(components)!!
    return formatter.stringFromDate(nsDate)
}
