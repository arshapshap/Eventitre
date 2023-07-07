package com.arshapshap.events.presentation.screens.calendar.calendarview.containers

import android.view.View
import com.arshapshap.events.databinding.CalendarDayLayoutBinding
import com.kizitonwose.calendar.view.ViewContainer
import java.time.LocalDate

class DayViewContainer(
    view: View,
    private val onClick: (LocalDate) -> Unit
) : ViewContainer(view) {

    lateinit var date: LocalDate
    val calendarDay = CalendarDayLayoutBinding.bind(view).calendarDay

    init {
        view.setOnClickListener {
            onClick.invoke(date)
        }
    }
}