package com.arshapshap.settings.data.repositories

import com.arshapshap.common.di.domain.models.Event
import com.arshapshap.database.dao.EventDao
import com.arshapshap.files.domain.FilesProvider
import com.arshapshap.settings.data.mappers.EventJsonMapper
import com.arshapshap.settings.data.mappers.EventMapper
import com.arshapshap.settings.domain.repositories.EventsJsonRepository
import javax.inject.Inject

internal class EventsJsonRepositoryImpl @Inject constructor(
    private val localSource: EventDao,
    private val filesProvider: FilesProvider,
    private val mapper: EventMapper,
    private val eventJsonMapper: EventJsonMapper
) : EventsJsonRepository {

    override suspend fun getEventsFromJson(callback: suspend (List<Event>) -> Unit) {
        filesProvider.getJsonFile {
            val list = eventJsonMapper.mapList(it)
            callback.invoke(list)
        }
    }

    override suspend fun getEventById(id: Long): Event? {
        return localSource.getById(id)?.let { mapper.map(it) }
    }

    override suspend fun addEvents(list: List<Event>): List<Long> {
        return localSource.addList(list.map { mapper.map(it) })
    }
}