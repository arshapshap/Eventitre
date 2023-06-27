package com.arshapshap.common_ui.extensions

import java.text.SimpleDateFormat
import java.util.*

fun Date.formatDayToString(): String {
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)

    val calendar = Calendar.getInstance()
    calendar.time = this
    val year = calendar.get(Calendar.YEAR)

    val outputDateFormatter =
        if (currentYear == year)
            SimpleDateFormat("HH:mm dd MMM", Locale.getDefault())
        else
            SimpleDateFormat("HH:mm dd MMM, yyyy", Locale.getDefault())

    return outputDateFormatter.format(this)
}