package com.arshapshap.events.presentation.screens.event

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.arshapshap.common.base.BaseViewModel
import com.arshapshap.common.base.ViewModelError
import com.arshapshap.common.base.ViewModelErrorLevel
import com.arshapshap.events.R
import com.arshapshap.events.domain.EventsInteractor
import com.arshapshap.events.domain.models.Event
import com.arshapshap.events.presentation.screens.EventsFeatureRouter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EventViewModel @AssistedInject constructor(
    @Assisted("ID") private val id: Int,
    private val interactor: EventsInteractor,
    private val router: EventsFeatureRouter
) : BaseViewModel() {

    private val _eventLiveData = MutableLiveData<Event>()
    internal val eventLiveData: LiveData<Event>
        get() = _eventLiveData

    private val _isEditingLiveData = MutableLiveData(false)
    internal val isEditingLiveData: LiveData<Boolean>
        get() = _isEditingLiveData

    private val _editingEventLiveData = MutableLiveData<Event?>()

    internal fun loadData() {
        _loadingLiveData.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            interactor.getEventById(id)?.let {
                _eventLiveData.postValue(it)
            } ?: run {
                _errorLiveData.postValue(
                    ViewModelError(
                        messageRes = R.string.event_not_found,
                        level = ViewModelErrorLevel.Error
                    )
                )
                closeFragment()
            }
        }
        _loadingLiveData.postValue(false)
    }

    private fun closeFragment() {
        router.closeCurrentFragment()
    }

    internal fun deleteEvent() {
        viewModelScope.launch {
            interactor.deleteEventById(id)
        }
        closeFragment()
    }

    internal fun setEditing(isEditing: Boolean) {
        if (!isEditing) {
            val newEvent = _editingEventLiveData.value ?: return
            if (newEvent.name.isEmpty()) {
                _errorLiveData.postValue(
                    ViewModelError(
                        messageRes = R.string.name_must_be_filled, level = ViewModelErrorLevel.Warn
                    )
                )
                return
            }
            _eventLiveData.postValue(newEvent)
        }
        _editingEventLiveData.postValue(if (isEditing) _eventLiveData.value else null)
        _isEditingLiveData.postValue(isEditing)
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

    @AssistedFactory
    interface Factory {

        fun create(@Assisted("ID") id: Int): EventViewModel
    }
}