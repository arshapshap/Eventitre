package com.arshapshap.events.domain

import com.arshapshap.events.domain.interfaces.EventsRepository
import com.arshapshap.events.domain.models.Event
import javax.inject.Inject

class EventsInteractor @Inject constructor(
    private val repository: EventsRepository
) {

    internal suspend fun getEvents(): List<Event> {
        return repository.getEvents()
    }

    internal suspend fun getEventById(id: Long): Event? {
        return repository.getEventById(id)
    }

    internal suspend fun deleteEventById(id: Long) {
        repository.deleteEventById(id)
    }
}