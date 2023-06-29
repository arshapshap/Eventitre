package com.arshapshap.events.presentation.screens.event

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.arshapshap.common.base.BaseViewModel
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

    internal fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            interactor.getEventById(id)?.let {
                _eventLiveData.postValue(it)
            } ?: run {
                _errorFromResourceLiveData.postValue(R.string.event_not_found)
            }
        }
    }

    internal fun closeFragment() {
        router.closeCurrentFragment()
    }

    internal fun deleteEvent() {
        viewModelScope.launch {
            interactor.deleteEventById(id)
        }
        router.closeCurrentFragment()
    }

    @AssistedFactory
    interface Factory {

        fun create(@Assisted("ID") id: Int): EventViewModel
    }
}