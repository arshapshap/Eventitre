package com.arshapshap.files.domain

import com.arshapshap.files.domain.models.EventJson

interface EventsJsonRepository {

    suspend fun getEventsFromJson(callback: suspend (List<EventJson>) -> Unit)
}