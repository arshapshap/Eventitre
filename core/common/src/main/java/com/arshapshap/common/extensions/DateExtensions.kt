package com.arshapshap.common.extensions

import java.lang.Math.abs
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

fun Date.toLocalDate(): LocalDate {
    return this.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
}

fun getMonthDifference(startDate: Date, endDate: Date): Int {
    val calendar1 = startDate.toCalendar()
    val calendar2 = endDate.toCalendar()

    val yearDiff = calendar2.get(Calendar.YEAR) - calendar1.get(Calendar.YEAR)
    val monthDiff = calendar2.get(Calendar.MONTH) - calendar1.get(Calendar.MONTH)

    return abs(yearDiff * 12 + monthDiff)
}

fun Date.getTimeInFloat(): Float {
    val calendar = this.toCalendar()
    val hours = calendar.get(Calendar.HOUR_OF_DAY)
    val minutes = calendar.get(Calendar.MINUTE)

    return hours.toFloat() + minutes.toFloat() / 60
}

fun Date.updateTime(date: Date): Date {
    val calendar = this.toCalendar()

    val currentTime = date.toCalendar()
    calendar.set(Calendar.HOUR_OF_DAY, currentTime.get(Calendar.HOUR_OF_DAY))
    calendar.set(Calendar.MINUTE, currentTime.get(Calendar.MINUTE))
    calendar.set(Calendar.SECOND, currentTime.get(Calendar.SECOND))
    calendar.set(Calendar.MILLISECOND, currentTime.get(Calendar.MILLISECOND))

    return calendar.time
}

fun Date.toCalendar(): Calendar {
    val calendar = Calendar.getInstance()
    calendar.time = this
    return calendar
}

fun Date.isDateInRange(dateStart: Date, dateFinish: Date): Boolean {
    val calendarStart = dateStart.toCalendar()
    val calendarFinish = dateFinish.toCalendar()
    val calendarToCheck = this.toCalendar()

    return (calendarToCheck in calendarStart..calendarFinish)
            || this.isSameDay(dateStart)
            || this.isSameDay(dateFinish)
}

fun Date.isSameDay(date: Date): Boolean {
    val calendar1 = this.toCalendar()
    val calendar2 = date.toCalendar()

    return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
            calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH) &&
            calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH)
}

fun Date.addHours(hours: Int): Date {
    val calendar = this.toCalendar()
    calendar.add(Calendar.HOUR_OF_DAY, hours)
    return calendar.time
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

fun Date.roundToDay(): Date {
    val calendar = Calendar.getInstance().apply {
        time = this@roundToDay
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return calendar.time
}