package com.arshapshap.settings.presentation.screens.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.arshapshap.common_ui.base.BaseViewModel
import com.arshapshap.common_ui.base.ViewModelError
import com.arshapshap.common_ui.base.ViewModelErrorLevel
import com.arshapshap.settings.R
import com.arshapshap.settings.domain.SettingsInteractor
import com.arshapshap.settings.domain.models.EventsImportInfo
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
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
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                val result = interactor.exportEvents()
                _exportedEventsLiveData.postValue(result.exportedNumber)
            }.onFailure {
                _errorLiveData.postValue(ViewModelError(
                    messageRes = com.arshapshap.common_ui.R.string.unexpected_error,
                    level = ViewModelErrorLevel.Message
                ))
            }
        }
    }

    internal fun requestImportEvents() {
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                val events = interactor.getEventsFromJson()
                _eventsToImportLiveData.postValue(events)
            }.onFailure {
                val error = when (it) {
                    is java.lang.NullPointerException -> ViewModelError(
                        messageRes = R.string.no_file_was_selected,
                        level = ViewModelErrorLevel.Message
                    )
                    else -> ViewModelError(
                        messageRes = com.arshapshap.common_ui.R.string.unexpected_error,
                        level = ViewModelErrorLevel.Message
                    )
                }
                _errorLiveData.postValue(error)
            }
        }
    }

    internal fun importEvents(withOverwriting: Boolean) {
        val list = if (withOverwriting) eventsToImportLiveData.value?.allEvents
            else eventsToImportLiveData.value?.newEvents

        viewModelScope.launch(Dispatchers.IO) {
            val result = interactor.importEvents(list ?: listOf())
            _importedEventsLiveData.postValue(result.importedNumber)
        }
    }

    internal fun clearAllData() {
        viewModelScope.launch(Dispatchers.IO) {
            interactor.clearAllData()
        }
    }

    @AssistedFactory
    interface Factory {

        fun create(): SettingsViewModel
    }
}