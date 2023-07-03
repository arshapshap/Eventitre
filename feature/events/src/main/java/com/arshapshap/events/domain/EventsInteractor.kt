package com.arshapshap.events.domain

import com.arshapshap.events.domain.repositories.EventsRepository
import com.arshapshap.common.di.domain.models.Event
import com.arshapshap.common_ui.extensions.isDateInRange
import java.util.Date
import javax.inject.Inject

class EventsInteractor @Inject constructor(
    private val repository: EventsRepository
) {

    internal suspend fun addEvent(event: Event): Long {
        return repository.addEvent(event)
    }

    internal suspend fun updateEvent(event: Event) {
        repository.updateEvent(event)
    }

    internal suspend fun getEvents(): List<Event> {
        return repository.getEvents()
    }

    internal suspend fun getEventsByDate(date: Date): List<Event> {
        return repository.getEventsByPredicate { date.isDateInRange(it.dateStart, it.dateFinish) }
    }

    internal suspend fun getEventById(id: Long): Event? {
        return repository.getEventById(id)
    }

    internal suspend fun deleteEventById(id: Long) {
        repository.deleteEventById(id)
    }
}