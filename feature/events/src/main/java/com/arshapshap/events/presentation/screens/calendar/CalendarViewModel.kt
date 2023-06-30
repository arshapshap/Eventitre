package com.arshapshap.events.presentation.screens.calendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.arshapshap.common_ui.base.BaseViewModel
import com.arshapshap.events.domain.EventsInteractor
import com.arshapshap.events.domain.models.Event
import com.arshapshap.events.presentation.screens.EventsFeatureRouter
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CalendarViewModel @AssistedInject constructor(
    private val interactor: EventsInteractor,
    private val router: EventsFeatureRouter
) : BaseViewModel() {

    private val _listLiveData = MutableLiveData<List<Event>>(listOf())
    internal val listLiveData: LiveData<List<Event>>
        get() = _listLiveData

    internal fun loadList() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = interactor.getEvents()
            _listLiveData.postValue(list)
        }
    }

    internal fun openEvent(event: Event) {
        router.openEvent(event.id)
    }

    internal fun openEventCreating() {
        router.openEventCreating()
    }

    @AssistedFactory
    interface Factory {

        fun create(): CalendarViewModel
    }
}