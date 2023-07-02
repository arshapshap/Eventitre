package com.arshapshap.settings.presentation.screens.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.arshapshap.common.di.domain.models.Event
import com.arshapshap.common_ui.base.BaseViewModel
import com.arshapshap.settings.domain.SettingsInteractor
import com.arshapshap.settings.domain.models.EventsExportInfo
import com.arshapshap.settings.domain.models.EventsImportInfo
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch

class SettingsViewModel @AssistedInject constructor(
    private val interactor: SettingsInteractor
) : BaseViewModel() {

    private val _eventsToImportLiveData = MutableLiveData<EventsImportInfo>()
    internal val eventsToImportLiveData: LiveData<EventsImportInfo>
        get() = _eventsToImportLiveData

    private val _importedEventsLiveData = MutableLiveData<Int>()
    internal val importedEventsLiveData: LiveData<Int>
        get() = _importedEventsLiveData

    private val _exportedEventsLiveData = MutableLiveData<Int>()
    internal val exportedEventsLiveData: LiveData<Int>
        get() = _exportedEventsLiveData

    internal fun exportEvents() {
        viewModelScope.launch {
            interactor.exportEvents(::handleExportResult)
        }
    }

    internal fun requestImportEvents() {
        viewModelScope.launch {
            interactor.requestImportEvents(::recieveEvents)
        }
    }

    internal fun importEventsWithOverwriting() {
        importEvents(eventsToImportLiveData.value?.list)
    }

    internal fun importOnlyNewEvents() {
        importEvents(eventsToImportLiveData.value?.listWithoutConflicts)
    }

    private fun importEvents(list: List<Event>?) {
        viewModelScope.launch {
            val added = interactor.importEvents(list ?: listOf())
            _importedEventsLiveData.postValue(added)
        }
    }

    private fun handleExportResult(result: EventsExportInfo) {
        _exportedEventsLiveData.postValue(result.exportedNumber)
    }

    private fun recieveEvents(events: EventsImportInfo) {
        _eventsToImportLiveData.postValue(events)
    }

    @AssistedFactory
    interface Factory {

        fun create(): SettingsViewModel
    }
}