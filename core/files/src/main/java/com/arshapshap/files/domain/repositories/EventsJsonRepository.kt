package com.arshapshap.files.domain.repositories

import com.arshapshap.files.domain.models.EventJson

interface EventsJsonRepository {

    suspend fun getEventsFromJson(): List<EventJson>

    suspend fun saveEventsInJson(events: List<EventJson>)
}