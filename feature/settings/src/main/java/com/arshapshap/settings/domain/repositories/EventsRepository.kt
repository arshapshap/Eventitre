package com.arshapshap.settings.domain.repositories

import com.arshapshap.common.di.domain.models.Event

interface EventsRepository {

    suspend fun getEventsFromJson(callback: suspend (List<Event>) -> Unit)

    suspend fun getEvents(): List<Event>

    suspend fun getEventById(id: Long): Event?

    suspend fun addEvents(list: List<Event>): List<Long>

    suspend fun exportEventsToJson(list: List<Event>, callback: () -> Unit)
}