package com.arshapshap.events.presentation.screens.event

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.arshapshap.common_ui.base.BaseViewModel
import com.arshapshap.common_ui.base.ViewModelError
import com.arshapshap.common_ui.base.ViewModelErrorLevel
import com.arshapshap.events.R
import com.arshapshap.events.domain.EventsInteractor
import com.arshapshap.common.di.domain.models.Event
import com.arshapshap.events.presentation.EventsFeatureRouter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class EventViewModel @AssistedInject constructor(
    @Assisted("ID") private val id: Long?,
    private val interactor: EventsInteractor,
    private val router: EventsFeatureRouter
) : BaseViewModel() {

    internal val isCreating
        get() = eventLiveData.value == null

    private val _eventLiveData = MutableLiveData<Event>()
    internal val eventLiveData: LiveData<Event>
        get() = _eventLiveData

    private val _isEditingLiveData = MutableLiveData(false)
    internal val isEditingLiveData: LiveData<Boolean>
        get() = _isEditingLiveData

    private val _editingEventLiveData = MutableLiveData<Event?>()
    internal val editingEventLiveData: LiveData<Event?>
        get() = _editingEventLiveData

    internal fun loadData() {
        _loadingLiveData.postValue(true)
        if (id == null) {
            val newEvent = Event(
                id = 0L,
                name = "",
                dateStart = Calendar.getInstance().time,
                dateFinish = Calendar.getInstance().time,
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
                    closeFragment()
                }
            }
        }
        _loadingLiveData.postValue(false)
    }

    internal fun deleteEvent() {
        if (_eventLiveData.value == null) return

        viewModelScope.launch {
            interactor.deleteEventById(_eventLiveData.value!!.id)
        }
        closeFragment()
    }

    internal fun applyChanges() {
        val newEvent = _editingEventLiveData.value ?: return
        if (newEvent.name.isEmpty()) {
            _errorLiveData.postValue(
                ViewModelError(
                    messageRes = R.string.name_must_be_filled, level = ViewModelErrorLevel.Warn
                )
            )
            return
        }

        if (isCreating) {
            viewModelScope.launch {
                val newEventId = interactor.addEvent(newEvent)
                _eventLiveData.postValue(newEvent.copy(id = newEventId))
            }
        } else {
            viewModelScope.launch {
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
            name = name
        )
        _editingEventLiveData.postValue(event)
    }

    internal fun setDescription(description: String) {
        if (isEditingLiveData.value != true) return
        val event = _editingEventLiveData.value?.copy(
            description = description
        )
        _editingEventLiveData.postValue(event)
    }

    internal fun setDateStart(calendar: Calendar) {
        if (isEditingLiveData.value != true) return

        val currentDateFinish = _editingEventLiveData.value?.dateFinish ?: return
        if (calendar.time.after(currentDateFinish)) {
            _errorLiveData.postValue(
                ViewModelError(
                    messageRes = R.string.start_date_later_than_finish_error,
                    level = ViewModelErrorLevel.Warn
                )
            )
            return
        }

        val event = _editingEventLiveData.value?.copy(
            dateStart = calendar.time
        )
        _editingEventLiveData.postValue(event)
    }

    internal fun setDateFinish(calendar: Calendar) {
        if (isEditingLiveData.value != true) return

        val currentDateStart = _editingEventLiveData.value?.dateStart ?: return
        if (calendar.time.before(currentDateStart)) {
            _errorLiveData.postValue(
                ViewModelError(
                    messageRes = R.string.start_date_later_than_finish_error,
                    level = ViewModelErrorLevel.Warn
                )
            )
            return
        }

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

        fun create(@Assisted("ID") id: Long?): EventViewModel
    }
}