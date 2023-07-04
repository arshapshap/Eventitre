package com.arshapshap.events.domain

import com.arshapshap.events.domain.repositories.EventsRepository
import com.arshapshap.common.di.domain.models.Event
import com.arshapshap.common_ui.extensions.addHours
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

    internal suspend fun getEventsByDateRange(dateStart: Date, dateFinish: Date): Map<Date, List<Event>> {
        val result = mutableMapOf<Date, List<Event>>()
        var date = dateStart
        val events = repository.getEvents()
        while (date < dateFinish) {
            val list = events.filter {
                date.isDateInRange(
                    it.dateStart, it.dateFinish
                )
            }
            if (list.isNotEmpty())
                result[date] = list

            date = date.addHours(24)
        }
        return result
    }

    internal suspend fun getEventById(id: Long): Event? {
        return repository.getEventById(id)
    }

    internal suspend fun deleteEventById(id: Long) {
        repository.deleteEventById(id)
    }
}