package com.arshapshap.events.data.repositories

import com.arshapshap.database.dao.EventDao
import com.arshapshap.events.data.mappers.EventMapper
import com.arshapshap.common.domain.models.Event
import com.arshapshap.events.domain.repositories.EventsRepository
import com.arshapshap.files.domain.repositories.EventsJsonRepository
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

internal class EventsRepositoryImpl @Inject constructor(
    private val localSource: EventDao,
    private val jsonRepository: EventsJsonRepository,
    private val mapper: EventMapper
) : EventsRepository {

    override suspend fun addEvent(event: Event): Long {
        return localSource.add(mapper.mapToLocal(event))
    }

    override suspend fun updateEvent(event: Event) {
        localSource.update(mapper.mapToLocal(event))
    }

    override suspend fun getEvents(): List<Event> {
        return localSource.getAll().map { mapper.mapFromLocal(it) }
    }

    override suspend fun getEventsByPredicate(predicate: (Event) -> Boolean): List<Event> {
        return localSource.getAll().map { mapper.mapFromLocal(it) }.filter(predicate)
    }

    override suspend fun getEventById(id: Long): Event? {
        return localSource.getById(id)?.let { mapper.mapFromLocal(it) }
    }

    override suspend fun deleteEventById(id: Long) {
        localSource.deleteById(id)
    }

    override suspend fun exportEventToJson(event: Event) = coroutineScope {
        return@coroutineScope jsonRepository.saveEventsInJson(
            events = listOf(mapper.mapToJson(event)),
            fileName = event.name
        )
    }
}