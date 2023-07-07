package com.arshapshap.common.domain.models

import com.arshapshap.common.domain.Constants
import com.arshapshap.common.extensions.toDate
import java.util.Date

data class Event(
    val id: Long,
    val dateStart: Date,
    val dateFinish: Date,
    val name: String,
    val description: String
)

object EventValidator {

    fun validateDates(dateStart: Date, dateFinish: Date): Boolean
        = dateStart.before(dateFinish)
            && dateStart.after(Constants.MIN_DATE.toDate())
            && dateFinish.before(Constants.MAX_DATE.toDate())
}