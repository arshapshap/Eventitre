package com.arshapshap.events.presentation.screens.event

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.arshapshap.common.base.BaseViewModel
import com.arshapshap.events.domain.EventsInteractor
import com.arshapshap.events.domain.models.Event
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch

class EventViewModel @AssistedInject constructor(
    @Assisted("ID") private val id: Int?,
    private val interactor: EventsInteractor
) : BaseViewModel() {

    private val _eventLiveData = MutableLiveData<Event>()
    internal val eventLiveData: LiveData<Event>
        get() = _eventLiveData

    internal fun loadData() {
        if (id != null)
            viewModelScope.launch {
                val event = interactor.getEventById(id) ?: return@launch
                _eventLiveData.postValue(event)
            }
    }

    @AssistedFactory
    interface Factory {

        fun create(@Assisted("ID") id: Int?): EventViewModel
    }
}