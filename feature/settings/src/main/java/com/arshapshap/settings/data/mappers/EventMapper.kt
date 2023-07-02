package com.arshapshap.settings.data.mappers

import com.arshapshap.database.models.EventEntity
import com.arshapshap.common.di.domain.models.Event
import java.util.*
import javax.inject.Inject

internal class EventMapper @Inject constructor() {

    fun map(eventLocal: EventEntity): Event =
        with (eventLocal) {
            Event(
                id = id,
                dateStart = Date(dateStartInMilliseconds),
                dateFinish = Date(dateFinishInMilliseconds),
                name = name,
                description = description
            )
        }

    fun map(event: Event): EventEntity =
        with (event) {
            EventEntity(
                id = id,
                dateStartInMilliseconds = dateStart.time,
                dateFinishInMilliseconds = dateFinish.time,
                name = name,
                description = description
            )
        }
}