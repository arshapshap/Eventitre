package com.arshapshap.events.presentation.screens.calendar.calendarview

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.children
import androidx.core.view.isGone
import com.arshapshap.common_ui.extensions.addMonths
import com.arshapshap.common_ui.extensions.formatToString
import com.arshapshap.common_ui.extensions.formatToStringWithYear
import com.arshapshap.common_ui.extensions.getColorAttributeFromTheme
import com.arshapshap.common_ui.extensions.isSameDay
import com.arshapshap.common_ui.extensions.toDate
import com.arshapshap.events.R
import com.arshapshap.events.presentation.screens.calendar.CalendarViewModel
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.Week
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.yearMonth
import com.kizitonwose.calendar.view.CalendarView
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.MonthScrollListener
import com.kizitonwose.calendar.view.WeekCalendarView
import com.kizitonwose.calendar.view.WeekDayBinder
import com.kizitonwose.calendar.view.WeekHeaderFooterBinder
import com.kizitonwose.calendar.view.WeekScrollListener
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Year
import java.util.Date

internal class CalendarManager(
    private val context: Context,
    private val viewModel: CalendarViewModel,
    private val calendarView: CalendarView,
    private val weekCalendarView: WeekCalendarView
) {

    var isMonthCalendarOnScreen: Boolean = true
        set(value) {
            calendarView.isGone = !value
            weekCalendarView.isGone = value
            field = value
        }

    fun setup() {
        configureCalendarView()
        configureWeekCalendarView()
    }

    fun notifyDateChanged(date: LocalDate) {
        calendarView.notifyDateChangedEverywhere(date)
        weekCalendarView.notifyDateChanged(date)
    }

    fun scrollToDate(date: LocalDate) {
        calendarView.scrollToDate(date)
        weekCalendarView.scrollToDate(date)
    }

    private fun configureWeekCalendarView() {
        with (weekCalendarView) {
            dayBinder = object : WeekDayBinder<DayViewContainer> {

                override fun create(view: View) = DayViewContainer(view) {
                    viewModel.selectDate(date = it.toDate())
                    smoothScrollToDate(it)
                }

                override fun bind(container: DayViewContainer, data: WeekDay) {
                    container.date = data.date
                    with (container.calendarDay) {
                        circleCount = viewModel.eventsLiveData.value?.get(data.date.toDate())?.size ?: 0

                        text = data.date.dayOfMonth.toString()
                        contentColor = getContentColorForDay(
                            date = data.date,
                            selectedDate = viewModel.selectedDateLiveData.value!!
                        )
                        background = getBackgroundForDay(
                            date = data.date,
                            selectedDate = viewModel.selectedDateLiveData.value!!
                        )
                    }
                }
            }

            weekHeaderBinder = object : WeekHeaderFooterBinder<MonthViewContainer> {

                override fun create(view: View) = MonthViewContainer(view)

                override fun bind(container: MonthViewContainer, data: Week) {
                    with (container.binding) {
                        val month1 = data.days.first().date.yearMonth
                        val month2 = data.days.last().date.yearMonth
                        if (month1 == month2)
                            calendarMonthTextView.text = month1.formatToString()
                        else {
                            if (month1.year == month2.year && month1.year == Year.now().value)
                                calendarMonthTextView.text = context.getString(
                                    R.string.two_months_placeholder,
                                    month1.formatToString(),
                                    month2.formatToString()
                                )
                            else
                                calendarMonthTextView.text = context.getString(
                                    R.string.two_months_placeholder,
                                    month1.formatToStringWithYear(shortMonth = true),
                                    month2.formatToStringWithYear(shortMonth = true)
                                )
                        }

                        dayTitlesContainer.children.map { it as TextView }.forEachIndexed { index, textView ->
                            val dayOfWeek = daysOfWeek()[index]
                            val title = dayOfWeek.getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.getDefault())
                            textView.text = title
                        }
                    }
                }
            }

            weekScrollListener = object : WeekScrollListener {
                override fun invoke(week: Week) {
                    loadData()
                }
            }

            setup(
                startDate = LocalDate.of(1970, 1, 1),
                endDate = LocalDate.of(2099, 12, 31),
                firstDayOfWeek = daysOfWeek().first()
            )
        }
    }

    private fun configureCalendarView() {
        with (calendarView) {
            dayBinder = object : MonthDayBinder<DayViewContainer> {

                override fun create(view: View) = DayViewContainer(view) {
                    viewModel.selectDate(date = it.toDate())
                    smoothScrollToDate(it)
                }

                override fun bind(container: DayViewContainer, data: CalendarDay) {
                    container.date = data.date
                    with (container.calendarDay) {
                        circleCount = viewModel.eventsLiveData.value?.get(data.date.toDate())?.size ?: 0

                        contentAlpha = if (data.position == DayPosition.MonthDate) 1f else 0.5f
                        text = data.date.dayOfMonth.toString()
                        contentColor = getContentColorForDay(
                            date = data.date,
                            selectedDate = viewModel.selectedDateLiveData.value!!,
                            inMonth = data.position == DayPosition.MonthDate
                        )
                        background = getBackgroundForDay(
                            date = data.date,
                            selectedDate = viewModel.selectedDateLiveData.value!!
                        )
                    }
                }
            }

            monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {

                override fun create(view: View) = MonthViewContainer(view)

                override fun bind(container: MonthViewContainer, data: CalendarMonth) {
                    with (container.binding) {
                        calendarMonthTextView.text = data.yearMonth.formatToString()
                        dayTitlesContainer.children.map { it as TextView }.forEachIndexed { index, textView ->
                            val dayOfWeek = daysOfWeek()[index]
                            val title = dayOfWeek.getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.getDefault())
                            textView.text = title
                        }
                    }
                }
            }

            monthScrollListener = object : MonthScrollListener {
                override fun invoke(calendarMonth: CalendarMonth) {
                    loadData()
                }
            }

            outDateStyle = com.kizitonwose.calendar.core.OutDateStyle.EndOfGrid
            setup(
                startMonth = java.time.YearMonth.of(1970, 1),
                endMonth = java.time.YearMonth.of(2099, 12),
                firstDayOfWeek = daysOfWeek().first()
            )
        }
    }

    private fun loadData() {
        val dateStart =
            if (isMonthCalendarOnScreen)
                calendarView.findFirstVisibleDay()?.date
            else
                weekCalendarView.findFirstVisibleDay()?.date
        val dateFinish =
            if (isMonthCalendarOnScreen)
                calendarView.findLastVisibleDay()?.date
            else
                weekCalendarView.findLastVisibleDay()?.date
        viewModel.loadData(
            dateStart = dateStart?.toDate()?.addMonths(-2)
                ?: viewModel.selectedDateLiveData.value
                ?: LocalDate.now().toDate(),
            dateFinish = dateFinish?.toDate()?.addMonths(2)
                ?: viewModel.selectedDateLiveData.value
                ?: LocalDate.now().toDate()
        )
    }

    @ColorInt
    private fun getContentColorForDay(date: LocalDate, selectedDate: Date, inMonth: Boolean? = null) =
        if (selectedDate.isSameDay(date.toDate()) && date == LocalDate.now() && inMonth != false)
            context.getColorAttributeFromTheme(com.google.android.material.R.attr.colorOnSecondary)
        else if (date == LocalDate.now() && inMonth != false)
            context.getColorAttributeFromTheme(com.google.android.material.R.attr.colorSecondary)
        else if (date.dayOfWeek == DayOfWeek.SUNDAY && inMonth != false)
            context.getColor(com.arshapshap.common_ui.R.color.red)
        else
            context.getColorAttributeFromTheme(com.google.android.material.R.attr.colorOnBackground)


    private fun getBackgroundForDay(date: LocalDate, selectedDate: Date) =
        if (selectedDate.isSameDay(date.toDate()) && date == LocalDate.now())
            AppCompatResources.getDrawable(context, R.drawable.bg_circle_filled)
        else if (selectedDate.isSameDay(date.toDate()))
            AppCompatResources.getDrawable(context, R.drawable.bg_circle)
        else null

    private fun CalendarView.notifyDateChangedEverywhere(localDate: LocalDate) {
        this.notifyDateChanged(localDate, DayPosition.InDate)
        this.notifyDateChanged(localDate, DayPosition.MonthDate)
        this.notifyDateChanged(localDate, DayPosition.OutDate)
    }
}