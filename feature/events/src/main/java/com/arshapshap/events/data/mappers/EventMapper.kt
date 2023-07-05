package com.arshapshap.events.data.mappers

import com.arshapshap.database.models.EventEntity
import com.arshapshap.common.domain.models.Event
import com.arshapshap.files.domain.models.EventJson
import java.util.*
import javax.inject.Inject

internal class EventMapper @Inject constructor() {

    fun mapFromLocal(eventLocal: EventEntity): Event =
        with (eventLocal) {
            Event(
                id = id,
                dateStart = Date(dateStartInMilliseconds),
                dateFinish = Date(dateFinishInMilliseconds),
                name = name,
                description = description
            )
        }

    fun mapToLocal(event: Event): EventEntity =
        with (event) {
            EventEntity(
                id = id,
                dateStartInMilliseconds = dateStart.time,
                dateFinishInMilliseconds = dateFinish.time,
                name = name,
                description = description
            )
        }

    internal fun mapToJson(event: Event): EventJson = with (event) {
        EventJson(
            id = id,
            dateStart = (dateStart.time / 1000).toString(),
            dateFinish = (dateFinish.time / 1000).toString(),
            name = name,
            description = description
        )
    }
}