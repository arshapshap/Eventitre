package com.arshapshap.settings.domain

import com.arshapshap.common.di.domain.models.Event
import com.arshapshap.settings.domain.models.EventsImportInfo
import com.arshapshap.settings.domain.repositories.EventsJsonRepository
import javax.inject.Inject

class SettingsInteractor @Inject constructor(
    private val repository: EventsJsonRepository
) {

    internal suspend fun requestImportEvents(callback: (EventsImportInfo) -> Unit) {
        repository.getEventsFromJson {
            val listWithoutConflicts = it.filter { repository.getEventById(it.id) == null }
            callback.invoke(
                EventsImportInfo(
                    list = it,
                    listWithoutConflicts = listWithoutConflicts
                )
            )
        }
    }

    internal suspend fun importEvents(list: List<Event>): Int {
        return repository.addEvents(list).size
    }
}