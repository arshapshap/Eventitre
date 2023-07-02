package com.arshapshap.events.domain.repositories

import com.arshapshap.common.di.domain.models.Event

interface EventsRepository {

    suspend fun addEvent(event: Event): Long

    suspend fun updateEvent(event: Event)

    suspend fun getEvents(): List<Event>

    suspend fun getEventsByPredicate(predicate: (Event) -> Boolean): List<Event>

    suspend fun getEventById(id: Long): Event?

    suspend fun deleteEventById(id: Long)
}