package com.arshapshap.events.presentation.screens.calendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.arshapshap.common.domain.models.Event
import com.arshapshap.common_ui.base.BaseViewModel
import com.arshapshap.common_ui.extensions.roundToDay
import com.arshapshap.common_ui.extensions.toDate
import com.arshapshap.common_ui.extensions.toLocalDate
import com.arshapshap.common_ui.extensions.updateTime
import com.arshapshap.events.domain.EventsInteractor
import com.arshapshap.events.presentation.EventsFeatureRouter
import com.kizitonwose.calendar.core.Week
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth
import java.util.Calendar
import java.util.Date

class CalendarViewModel @AssistedInject constructor(
    private val interactor: EventsInteractor,
    private val router: EventsFeatureRouter
) : BaseViewModel() {

    private var _loadedYears = mutableSetOf(Year.now())
    val loadedYears: List<Year>
        get() = _loadedYears.toList()

    private val _eventsLiveData = MutableLiveData<Map<Date, List<Event>>>()
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

    private val _isCalendarExpandedLiveData = MutableLiveData(true)
    internal val isCalendarExpandedLiveData: LiveData<Boolean>
        get() = _isCalendarExpandedLiveData

    internal fun loadDataInitial() {
        _loadingLiveData.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            val events = mutableMapOf<Date, List<Event>>()
            loadedYears.forEach {
                val dateStart = LocalDate.ofYearDay(it.value, 1).toDate()
                val dateFinish = LocalDate.ofYearDay(it.value+1, 1).toDate()

                events += interactor.getEventsByDateRange(dateStart, dateFinish)
            }

            eventsLiveData.value?.let {
                val changed = compareEventMaps(it, events)
                _changedLiveData.postValue(changed)
            }
            _eventsLiveData.postValue(events)
            _listLiveData.postValue(events[_selectedDateLiveData.value?.roundToDay()] ?: listOf())
            _loadingLiveData.postValue(false)
        }
    }

    internal fun loadDataAdditional(year: Year) {
        if (_loadedYears.contains(year)) return

        _loadingLiveData.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            val dateStart = LocalDate.ofYearDay(year.value, 1).toDate()
            val dateFinish = LocalDate.ofYearDay(year.value+1, 1).toDate()

            val events = interactor.getEventsByDateRange(dateStart, dateFinish) + (eventsLiveData.value ?: mapOf())
            _eventsLiveData.postValue(events)
            _listLiveData.postValue(events[_selectedDateLiveData.value?.roundToDay()] ?: listOf())
            _loadingLiveData.postValue(false)
            _loadedYears.add(year)
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

    internal fun openMonth(month: YearMonth) {
        val date = selectedDateLiveData.value?.toLocalDate()?.withMonth(month.monthValue)?.withYear(month.year) ?: return
        selectDate(date.toDate())
    }

    internal fun openWeek(week: Week) {
        val weekDay = selectedDateLiveData.value?.toLocalDate()?.dayOfWeek ?: return
        val date = week.days[weekDay.value - 1].date
        selectDate(date.toDate())
    }

    internal fun changeCalendarView() {
        val isCalendarExpanded = isCalendarExpandedLiveData.value ?: return
        _isCalendarExpandedLiveData.postValue(!isCalendarExpanded)
    }

    private fun compareEventMaps(old: Map<Date, List<Event>>, new: Map<Date, List<Event>>): List<Date> {
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