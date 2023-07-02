package com.arshapshap.settings.data.repositories

import com.arshapshap.common.di.domain.models.Event
import com.arshapshap.database.dao.EventDao
import com.arshapshap.files.domain.repositories.EventsJsonRepository
import com.arshapshap.settings.data.mappers.EventMapper
import com.arshapshap.settings.domain.repositories.EventsRepository
import javax.inject.Inject

internal class EventsRepositoryImpl @Inject constructor(
    private val localSource: EventDao,
    private val jsonRepository: EventsJsonRepository,
    private val mapper: EventMapper
) : EventsRepository {

    override suspend fun getEventsFromJson(callback: suspend (List<Event>) -> Unit) {
        jsonRepository.getEventsFromJson {
            val list = mapper.mapListFromJson(it)
            callback.invoke(list)
        }
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

    override suspend fun exportEventsToJson(list: List<Event>, callback: () -> Unit) {
        jsonRepository.saveEventsInJson(mapper.mapListToJson(list), callback)
    }
}