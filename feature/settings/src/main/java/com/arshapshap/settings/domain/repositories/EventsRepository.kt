package com.arshapshap.settings.domain.repositories

import com.arshapshap.common.domain.models.Event

interface EventsRepository {

    suspend fun getEventsFromJson(): List<Event>

    suspend fun exportEventsToJson(list: List<Event>)

    suspend fun getEvents(): List<Event>

    suspend fun getEventById(id: Long): Event?

    suspend fun addEvents(list: List<Event>): List<Long>
}