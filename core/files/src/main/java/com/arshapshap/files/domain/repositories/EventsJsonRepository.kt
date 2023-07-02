package com.arshapshap.files.domain.repositories

import com.arshapshap.files.domain.models.EventJson

interface EventsJsonRepository {

    suspend fun getEventsFromJson(callback: suspend (List<EventJson>) -> Unit)

    suspend fun saveEventsInJson(events: List<EventJson>, callback: () -> Unit)
}