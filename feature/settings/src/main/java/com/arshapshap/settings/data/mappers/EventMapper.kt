package com.arshapshap.settings.data.mappers

import com.arshapshap.database.models.EventEntity
import com.arshapshap.common.di.domain.models.Event
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

    internal fun mapListFromJson(eventsJson: List<EventJson>): List<Event> {
        return eventsJson
            .filter {
                it.dateStart != null && it.dateFinish != null && it.name != null
            }
            .map { this.mapFromJson(it) }
            .toList()
    }

    internal fun mapListToJson(events: List<Event>): List<EventJson> {
        return events.map { this.mapToJson(it) }
    }

    private fun mapToJson(event: Event): EventJson {
        return with (event) {
            EventJson(
                id = id,
                dateStart = (dateStart.time / 1000).toString(),
                dateFinish = (dateFinish.time / 1000).toString(),
                name = name,
                description = description
            )
        }
    }

    private fun mapFromJson(eventJson: EventJson): Event {
        return with (eventJson) {
            Event(
                id = id ?: 0,
                dateStart = Date(dateStart!!.toLong() * 1000),
                dateFinish = Date(dateFinish!!.toLong() * 1000),
                name = name!!,
                description = description ?: ""
            )
        }
    }
}