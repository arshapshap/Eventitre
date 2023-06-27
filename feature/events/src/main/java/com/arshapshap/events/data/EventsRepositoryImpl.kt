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
            description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
        ),
        Event(
            id = 3,
            dateStart = Date(1571000494000),
            dateFinish = Date(1571400494000),
            name = "Event 3",
            description = ""
        ),
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