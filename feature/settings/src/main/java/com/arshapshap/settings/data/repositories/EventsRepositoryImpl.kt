package com.arshapshap.settings.data.repositories

import com.arshapshap.common.domain.models.Event
import com.arshapshap.database.dao.EventDao
import com.arshapshap.files.domain.repositories.EventsJsonRepository
import com.arshapshap.settings.data.mappers.EventMapper
import com.arshapshap.settings.domain.repositories.EventsRepository
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

internal class EventsRepositoryImpl @Inject constructor(
    private val localSource: EventDao,
    private val jsonRepository: EventsJsonRepository,
    private val mapper: EventMapper
) : EventsRepository {

    companion object {

        private const val EXPORTED_JSON_FILE_NAME = "exported_events"
    }

    override suspend fun getEventsFromJson(): List<Event> = coroutineScope {
        return@coroutineScope jsonRepository.getEventsFromJson()
            .filter {
                it.dateStart != null && it.dateFinish != null && it.name != null
            }
            .map { mapper.mapFromJson(it) }
    }

    override suspend fun exportEventsToJson(list: List<Event>) = coroutineScope {
        return@coroutineScope jsonRepository.saveEventsInJson(
            events = list.map { mapper.mapToJson(it) },
            fileName = EXPORTED_JSON_FILE_NAME
        )
    }

    override suspend fun getEvents(): List<Event> {
        return localSource.getAll().map { mapper.mapFromLocal(it) }
    }

    override suspend fun getEventById(id: Long): Event? {
        return localSource.getById(id)?.let { mapper.mapFromLocal(it) }
    }

    override suspend fun addEvents(list: List<Event>): List<Long> {
        return localSource.addList(list.map { mapper.mapToLocal(it) })
    }
}