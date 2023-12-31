package com.arshapshap.events.presentation.screens.event

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.arshapshap.common_ui.base.BaseViewModel
import com.arshapshap.common_ui.base.ViewModelError
import com.arshapshap.common_ui.base.ViewModelErrorLevel
import com.arshapshap.events.R
import com.arshapshap.events.domain.EventsInteractor
import com.arshapshap.common.domain.models.Event
import com.arshapshap.common.domain.models.EventValidator
import com.arshapshap.common.extensions.addHours
import com.arshapshap.events.presentation.EventsFeatureRouter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class EventViewModel @AssistedInject constructor(
    @Assisted("ID") private val id: Long?,
    @Assisted("DATE") private val date: Date?,
    private val interactor: EventsInteractor,
    private val router: EventsFeatureRouter
) : BaseViewModel() {

    internal val isCreating
        get() = eventLiveData.value == null

    private val _eventLiveData = MutableLiveData<Event>()
    internal val eventLiveData: LiveData<Event>
        get() = _eventLiveData

    private val _isEditingLiveData = MutableLiveData<Boolean>()
    internal val isEditingLiveData: LiveData<Boolean>
        get() = _isEditingLiveData

    private val _editingEventLiveData = MutableLiveData<Event?>()
    internal val editingEventLiveData: LiveData<Event?>
        get() = _editingEventLiveData

    internal fun loadData() {
        _loadingLiveData.postValue(true)
        if (id == null || id == 0L) {
            val newEvent = Event(
                id = 0L,
                name = "",
                dateStart = date ?: Calendar.getInstance().time,
                dateFinish = (date ?: Calendar.getInstance().time).addHours(1),
                description = ""
            )
            _isEditingLiveData.postValue(true)
            _editingEventLiveData.postValue(newEvent)
        }
        else {
            viewModelScope.launch(Dispatchers.IO) {
                interactor.getEventById(id)?.let {
                    _eventLiveData.postValue(it)
                } ?: run {
                    _errorLiveData.postValue(
                        ViewModelError(
                            messageRes = R.string.event_not_found, level = ViewModelErrorLevel.Error
                        )
                    )
                    withContext(Dispatchers.Main) {
                        closeFragment()
                    }
                }
                _isEditingLiveData.postValue(false)
            }
        }
        _loadingLiveData.postValue(false)
    }

    internal fun exportEvent() {
        viewModelScope.launch(Dispatchers.IO) {
            val event = _eventLiveData.value ?: return@launch
            kotlin.runCatching {
                interactor.exportEvent(event.id)
                _errorLiveData.postValue(ViewModelError(
                    messageRes = R.string.successfully_exported,
                    level = ViewModelErrorLevel.Message
                ))
            }.onFailure {
                _errorLiveData.postValue(ViewModelError(
                    messageRes = com.arshapshap.common_ui.R.string.unexpected_error,
                    level = ViewModelErrorLevel.Message
                ))
            }
        }
    }

    internal fun deleteEvent() {
        if (_eventLiveData.value == null) return

        viewModelScope.launch(Dispatchers.IO) {
            interactor.deleteEventById(_eventLiveData.value!!.id)
        }
        closeFragment()
    }

    internal fun applyChanges() {
        val newEvent = _editingEventLiveData.value ?: return
        if (newEvent.name.isEmpty()) {
            _errorLiveData.postValue(
                ViewModelError(
                    messageRes = R.string.name_must_be_filled, level = ViewModelErrorLevel.Message
                )
            )
            return
        }
        else if (!EventValidator.validateDates(newEvent.dateStart, newEvent.dateFinish)) {
            _errorLiveData.postValue(
                ViewModelError(
                    messageRes = R.string.start_must_be_earlier_than_finish_error,
                    level = ViewModelErrorLevel.Message
                )
            )
            return
        }

        if (isCreating) {
            viewModelScope.launch(Dispatchers.IO) {
                val newEventId = interactor.addEvent(newEvent)
                _eventLiveData.postValue(newEvent.copy(id = newEventId))
            }
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                interactor.updateEvent(newEvent)
                _eventLiveData.postValue(newEvent)
            }
        }
        _isEditingLiveData.postValue(false)
    }

    internal fun editEvent() {
        _editingEventLiveData.postValue(_eventLiveData.value)
        _isEditingLiveData.postValue(true)
    }

    internal fun setName(name: String) {
        if (isEditingLiveData.value != true) return
        val event = _editingEventLiveData.value?.copy(
            name = name.trim()
        )
        _editingEventLiveData.postValue(event)
    }

    internal fun setDescription(description: String) {
        if (isEditingLiveData.value != true) return
        val event = _editingEventLiveData.value?.copy(
            description = description.trim()
        )
        _editingEventLiveData.postValue(event)
    }

    internal fun setDateStart(calendar: Calendar) {
        if (isEditingLiveData.value != true) return

        val event = _editingEventLiveData.value?.copy(
            dateStart = calendar.time
        )
        _editingEventLiveData.postValue(event)
    }

    internal fun setDateFinish(calendar: Calendar) {
        if (isEditingLiveData.value != true) return

        val event = _editingEventLiveData.value?.copy(
            dateFinish = calendar.time
        )
        _editingEventLiveData.postValue(event)
    }

    private fun closeFragment() {
        router.closeCurrentFragment()
    }

    @AssistedFactory
    interface Factory {

        fun create(@Assisted("ID") id: Long?, @Assisted("DATE") date: Date?): EventViewModel
    }
}