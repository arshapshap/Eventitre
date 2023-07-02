package com.arshapshap.files.data.repositories

import com.arshapshap.files.data.mappers.EventJsonMapper
import com.arshapshap.files.domain.repositories.EventsJsonRepository
import com.arshapshap.files.domain.FilesReader
import com.arshapshap.files.domain.FilesWriter
import com.arshapshap.files.domain.models.EventJson
import javax.inject.Inject

internal class EventsJsonRepositoryImpl @Inject constructor(
    private val filesReader: FilesReader,
    private val filesWriter: FilesWriter,
    private val mapper: EventJsonMapper
) : EventsJsonRepository {

    override suspend fun getEventsFromJson(callback: suspend (List<EventJson>) -> Unit) {
        filesReader.getJsonFile {
            callback.invoke(mapper.mapFromJsonString(it))
        }
    }

    override suspend fun saveEventsInJson(events: List<EventJson>, callback: () -> Unit) {
        filesWriter.createJson(mapper.mapToJsonString(events), callback)
    }
}