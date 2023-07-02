package com.arshapshap.events.data.repositories

import com.arshapshap.database.dao.EventDao
import com.arshapshap.events.data.mappers.EventMapper
import com.arshapshap.common.di.domain.models.Event
import com.arshapshap.events.domain.repositories.EventsRepository
import javax.inject.Inject

internal class EventsRepositoryImpl @Inject constructor(
    private val localSource: EventDao,
    private val mapper: EventMapper
) : EventsRepository {

    override suspend fun addEvent(event: Event): Long {
        return localSource.add(mapper.map(event))
    }

    override suspend fun updateEvent(event: Event) {
        localSource.update(mapper.map(event))
    }

    override suspend fun getEvents(): List<Event> {
        return localSource.getAll().map { mapper.map(it) }
    }

    override suspend fun getEventsByPredicate(predicate: (Event) -> Boolean): List<Event> {
        return localSource.getAll().map { mapper.map(it) }.filter(predicate)
    }

    override suspend fun getEventById(id: Long): Event? {
        return localSource.getById(id)?.let { mapper.map(it) }
    }

    override suspend fun deleteEventById(id: Long) {
        localSource.deleteById(id)
    }
}