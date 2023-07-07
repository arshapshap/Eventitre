package com.arshapshap.settings.domain

import com.arshapshap.common.domain.models.Event
import com.arshapshap.settings.domain.models.EventsExportResult
import com.arshapshap.settings.domain.models.EventsImportInfo
import com.arshapshap.settings.domain.models.EventsImportResult
import com.arshapshap.settings.domain.repositories.EventsRepository
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class SettingsInteractor @Inject constructor(
    private val repository: EventsRepository
) {

    internal suspend fun getEventsFromJson(): EventsImportInfo = coroutineScope {
        val events = repository.getEventsFromJson()
        val eventsWithoutConflicts = events.filter { repository.getEventById(it.id) == null }
        return@coroutineScope EventsImportInfo(
            allEvents = events, newEvents = eventsWithoutConflicts
        )
    }

    internal suspend fun importEvents(list: List<Event>): EventsImportResult {
        return EventsImportResult(
            importedNumber = repository.addEvents(list).size
        )
    }

    internal suspend fun exportEvents(): EventsExportResult = coroutineScope {
        val events = repository.getEvents()
        if (events.isEmpty()) return@coroutineScope EventsExportResult(0)

        repository.exportEventsToJson(events)
        return@coroutineScope EventsExportResult(
            exportedNumber = events.size
        )
    }

    internal suspend fun clearAllData() {
       repository.deleteAllEvents()
    }
}