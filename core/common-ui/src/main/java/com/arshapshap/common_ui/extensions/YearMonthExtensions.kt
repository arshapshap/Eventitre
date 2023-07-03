package com.arshapshap.common_ui.extensions

import java.text.SimpleDateFormat
import java.time.YearMonth
import java.util.*

fun YearMonth.formatToString(): String {
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)

    val outputDateFormatter =
        if (currentYear == year)
            SimpleDateFormat("LLLL", Locale.getDefault())
        else
            SimpleDateFormat("LLLL yyyy", Locale.getDefault())
    return outputDateFormatter.format(this.toDate())
}

fun YearMonth.toDate(): Date {
    val calendar = Calendar.getInstance()
    calendar.clear()
    calendar.set(Calendar.MONTH, month.value - 1)
    calendar[Calendar.YEAR] = year
    return calendar.time
}