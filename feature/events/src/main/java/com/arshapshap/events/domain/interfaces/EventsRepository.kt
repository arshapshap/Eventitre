package com.arshapshap.events.domain.interfaces

import com.arshapshap.events.domain.models.Event

interface EventsRepository {

    suspend fun getEvents(): List<Event>

    suspend fun getEventsByPredicate(predicate: (Event) -> Boolean): List<Event>

    suspend fun getEventById(id: Int): Event?

    suspend fun deleteEventById(id: Int)
}