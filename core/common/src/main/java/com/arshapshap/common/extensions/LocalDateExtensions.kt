package com.arshapshap.common.extensions

import java.time.LocalDate
import java.time.ZoneId
import java.util.*

fun LocalDate.toDate(): Date {
    return Date.from(this.atStartOfDay(ZoneId.systemDefault()).toInstant())
}