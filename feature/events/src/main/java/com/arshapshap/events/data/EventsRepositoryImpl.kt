package com.arshapshap.events.data

import com.arshapshap.events.domain.interfaces.EventsRepository
import com.arshapshap.events.domain.models.Event
import java.util.*
import javax.inject.Inject

class EventsRepositoryImpl @Inject constructor() : EventsRepository {

    private val list = listOf(
        Event(
            id = 1,
            dateStart = Date(1510500494000),
            dateFinish = Date(1510510494000),
            name = "Event 1",
            description = "Something is happening here"
        ),
        Event(
            id = 2,
            dateStart = Date(1551000494000),
            dateFinish = Date(1551010494000),
            name = "Event 2",
            description = "Something is happening here too"
        )
    )

    override suspend fun getEvents(): List<Event> {
        return list
    }

    override suspend fun getEventsByPredicate(predicate: (Event) -> Boolean): List<Event> {
        return list.filter(predicate)
    }

    override suspend fun getEventById(id: Int): Event? {
        return list.find { it.id == id }
    }
}