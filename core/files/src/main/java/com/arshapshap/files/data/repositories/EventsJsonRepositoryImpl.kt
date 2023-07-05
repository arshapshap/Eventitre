package com.arshapshap.files.data.repositories

import com.arshapshap.files.data.mappers.EventJsonMapper
import com.arshapshap.files.domain.FilesReader
import com.arshapshap.files.domain.FilesWriter
import com.arshapshap.files.domain.models.EventJson
import com.arshapshap.files.domain.repositories.EventsJsonRepository
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

internal class EventsJsonRepositoryImpl @Inject constructor(
    private val filesReader: FilesReader,
    private val filesWriter: FilesWriter,
    private val mapper: EventJsonMapper
) : EventsJsonRepository {

    override suspend fun getEventsFromJson(): List<EventJson> = coroutineScope {
        val json = filesReader.getJsonFile()
        return@coroutineScope mapper.mapFromJsonString(json)
    }

    override suspend fun saveEventsInJson(events: List<EventJson>, fileName: String) = coroutineScope {
        return@coroutineScope filesWriter.createJson(
            json = mapper.mapToJsonString(events),
            fileName = "$fileName.json"
        )
    }
}