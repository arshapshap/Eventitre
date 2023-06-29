package com.arshapshap.common_ui.extensions

import java.text.SimpleDateFormat
import java.util.*

fun Date.toCalendar(): Calendar {
    val calendar = Calendar.getInstance()
    calendar.time = this
    return calendar
}

fun Date.formatDateTimeToString(): String {
    val timeString = this.formatTimeToString()
    val dateString = this.formatDateToString()
    return "$timeString $dateString"
}

fun Date.formatDateToString(): String {
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val year = this.toCalendar().get(Calendar.YEAR)

    val outputDateFormatter =
        if (currentYear == year)
            SimpleDateFormat("dd MMM", Locale.getDefault())
        else
            SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
    return outputDateFormatter.format(this)
}

fun Date.formatTimeToString(): String {
    val outputDateFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    return outputDateFormatter.format(this)
}