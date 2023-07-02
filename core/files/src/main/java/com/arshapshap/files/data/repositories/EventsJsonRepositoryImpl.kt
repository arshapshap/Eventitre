package com.arshapshap.files.data.repositories

import com.arshapshap.files.data.mappers.EventJsonMapper
import com.arshapshap.files.domain.EventsJsonRepository
import com.arshapshap.files.domain.FilesProvider
import com.arshapshap.files.domain.models.EventJson
import javax.inject.Inject

internal class EventsJsonRepositoryImpl @Inject constructor(
    private val filesProvider: FilesProvider,
    private val mapper: EventJsonMapper
) : EventsJsonRepository {

    override suspend fun getEventsFromJson(callback: suspend (List<EventJson>) -> Unit) {
        filesProvider.getJsonFile {
            callback.invoke(mapper.mapFromJsonString(it))
        }
    }
}