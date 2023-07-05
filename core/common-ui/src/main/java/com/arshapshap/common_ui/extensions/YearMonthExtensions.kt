package com.arshapshap.common_ui.extensions

import java.text.SimpleDateFormat
import java.time.YearMonth
import java.util.*

fun YearMonth.formatToString(shortMonth: Boolean = false): String {
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)

    val monthPattern = if (shortMonth) "LLL" else "LLLL"
    val pattern = if (currentYear == year) monthPattern else "$monthPattern yyyy"
    val outputDateFormatter = SimpleDateFormat(pattern, Locale.getDefault())
    return outputDateFormatter.format(this.toDate()).replaceFirstChar(Char::titlecase)
}

fun YearMonth.formatToStringWithYear(shortMonth: Boolean = false): String {
    val monthPattern = if (shortMonth) "LLL" else "LLLL"
    val pattern = "$monthPattern yyyy"
    val outputDateFormatter = SimpleDateFormat(pattern, Locale.getDefault())
    return outputDateFormatter.format(this.toDate()).replaceFirstChar(Char::titlecase)
}

fun YearMonth.toDate(): Date {
    val calendar = Calendar.getInstance()
    calendar.clear()
    calendar.set(Calendar.MONTH, month.value - 1)
    calendar[Calendar.YEAR] = year
    return calendar.time
}