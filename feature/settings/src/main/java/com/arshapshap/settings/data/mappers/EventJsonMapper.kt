package com.arshapshap.settings.data.mappers

import com.arshapshap.common.di.domain.models.Event
import com.arshapshap.settings.data.models.EventJson
import com.google.gson.Gson
import java.util.*
import javax.inject.Inject

internal class EventJsonMapper @Inject constructor(
    private val gson: Gson
) {

    internal fun mapList(json: String): List<Event> {
        return gson.fromJson(json, Array<EventJson>::class.java)
            .toList()
            .filter {
                it.dateStart != null && it.dateFinish != null && it.name != null
            }
            .map { this.map(it) }
            .toList()
    }

    internal fun mapList(events: List<Event>): String {
        return gson.toJson(events.map { this.map(it) })
    }

    private fun map(event: Event): EventJson {
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

    private fun map(eventJson: EventJson): Event {
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