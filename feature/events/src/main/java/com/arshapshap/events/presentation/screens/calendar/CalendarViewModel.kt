package com.arshapshap.events.presentation.screens.calendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.arshapshap.common_ui.base.BaseViewModel
import com.arshapshap.events.domain.EventsInteractor
import com.arshapshap.common.domain.models.Event
import com.arshapshap.common_ui.extensions.roundToDay
import com.arshapshap.common_ui.extensions.updateTime
import com.arshapshap.events.presentation.EventsFeatureRouter
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class CalendarViewModel @AssistedInject constructor(
    private val interactor: EventsInteractor,
    private val router: EventsFeatureRouter
) : BaseViewModel() {

    private val _eventsLiveData = MutableLiveData<Map<Date, List<Event>>>(mapOf())
    internal val eventsLiveData: LiveData<Map<Date, List<Event>>>
        get() = _eventsLiveData

    private val _changedLiveData = MutableLiveData<List<Date>>()
    internal val changedLiveData: LiveData<List<Date>>
        get() = _changedLiveData

    private val _listLiveData = MutableLiveData<List<Event>>()
    internal val listLiveData: LiveData<List<Event>>
        get() = _listLiveData

    private val _unselectedDateLiveData = MutableLiveData<Date>()
    internal val unselectedDateLiveData: LiveData<Date>
        get() = _unselectedDateLiveData

    private val _selectedDateLiveData = MutableLiveData(Calendar.getInstance().time)
    internal val selectedDateLiveData: LiveData<Date>
        get() = _selectedDateLiveData

    internal fun loadData(dateStart: Date, dateFinish: Date) {
        viewModelScope.launch(Dispatchers.IO) {
            val events = interactor.getEventsByDateRange(dateStart, dateFinish)
            eventsLiveData.value?.let {
                val changed = compareEventMaps(it, events)
                _changedLiveData.postValue(changed)
            }
            _eventsLiveData.postValue(events)
            _listLiveData.postValue(events[_selectedDateLiveData.value?.roundToDay()] ?: listOf())
            _loadingLiveData.postValue(false)
        }
    }

    internal fun openEvent(event: Event) {
        router.openEvent(event.id)
    }

    internal fun openEventCreating() {
        router.openEventCreating(selectedDateLiveData.value!!.updateTime(Calendar.getInstance().time))
    }

    internal fun selectDate(date: Date) {
        viewModelScope.launch(Dispatchers.IO) {
            _unselectedDateLiveData.postValue(selectedDateLiveData.value)
            _selectedDateLiveData.postValue(date)
            _listLiveData.postValue(eventsLiveData.value?.get(date) ?: listOf())
        }
    }

    fun compareEventMaps(old: Map<Date, List<Event>>, new: Map<Date, List<Event>>): List<Date> {
        val changedDates = mutableListOf<Date>()

        for ((date, oldEvents) in old) {
            val newEvents = new[date]
            if (newEvents == null || !oldEvents.containsAll(newEvents)) {
                changedDates.add(date)
            }
        }

        return changedDates
    }

    @AssistedFactory
    interface Factory {

        fun create(): CalendarViewModel
    }
}