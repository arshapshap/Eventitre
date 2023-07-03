package com.arshapshap.events.presentation.screens.calendar

import android.view.View
import com.arshapshap.events.databinding.CalendarDayLayoutBinding
import com.kizitonwose.calendar.view.ViewContainer
import java.time.LocalDate

class DayViewContainer(
    view: View,
    private val onClick: (LocalDate) -> Unit
) : ViewContainer(view) {

    lateinit var date: LocalDate
    val binding = CalendarDayLayoutBinding.bind(view)

    init {
        view.setOnClickListener {
            onClick.invoke(date)
        }
    }
}