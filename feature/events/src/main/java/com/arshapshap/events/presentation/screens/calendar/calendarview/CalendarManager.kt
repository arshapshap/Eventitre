package com.arshapshap.events.presentation.screens.calendar.calendarview

import android.animation.ValueAnimator
import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.arshapshap.common.extensions.formatToString
import com.arshapshap.common.extensions.formatToStringWithYear
import com.arshapshap.common_ui.extensions.getColorAttributeFromTheme
import com.arshapshap.common.extensions.isSameDay
import com.arshapshap.common.extensions.toDate
import com.arshapshap.common.extensions.toLocalDate
import com.arshapshap.events.R
import com.arshapshap.common.domain.Constants
import com.arshapshap.events.presentation.screens.calendar.CalendarViewModel
import com.arshapshap.events.presentation.screens.calendar.calendarview.containers.DayViewContainer
import com.arshapshap.events.presentation.screens.calendar.calendarview.containers.CalendarHeaderContainer
import com.arshapshap.events.presentation.screens.calendar.calendarview.customview.CalendarDayView
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.OutDateStyle
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
import java.time.YearMonth
import java.util.Date

internal class CalendarManager(
    private val context: Context,
    private val viewModel: CalendarViewModel,
    private val monthCalendarView: CalendarView,
    private val weekCalendarView: WeekCalendarView
) {

    var isCalendarExpanded: Boolean = false
        set(value) {
            changeCalendarViewWithAnimation(value)
            field = value
        }

    private var animator: ValueAnimator? = null

    fun setup() {
        configureCalendarView()
        configureWeekCalendarView()
    }

    fun notifyDateChanged(date: Date) {
        monthCalendarView.notifyDateChanged(date.toLocalDate(), DayPosition.InDate)
        monthCalendarView.notifyDateChanged(date.toLocalDate(), DayPosition.MonthDate)
        monthCalendarView.notifyDateChanged(date.toLocalDate(), DayPosition.OutDate)
        weekCalendarView.notifyDateChanged(date.toLocalDate())
    }

    fun scrollToDate(date: Date) {
        monthCalendarView.scrollToDate(date.toLocalDate())
        weekCalendarView.scrollToDate(date.toLocalDate())
    }

    fun smoothScrollToDate(date: Date) {
        if (isCalendarExpanded) {
            monthCalendarView.smoothScrollToDate(date.toLocalDate())
            weekCalendarView.scrollToDate(date.toLocalDate())
        } else {
            weekCalendarView.smoothScrollToDate(date.toLocalDate())
            monthCalendarView.scrollToDate(date.toLocalDate())
        }
    }

    private fun configureCalendarView() {
        with (monthCalendarView) {
            dayBinder = getMonthDayBinder()
            monthHeaderBinder = getMonthHeaderBinder()
            monthScrollListener = object : MonthScrollListener {

                override fun invoke(month: CalendarMonth) {
                    viewModel.loadDataAdditional(Year.of(month.yearMonth.year))

                    viewModel.openMonth(month.yearMonth)
                }
            }

            outDateStyle = OutDateStyle.EndOfGrid
            setup(
                startMonth = YearMonth.of(1970, 1),
                endMonth = YearMonth.of(2099, 12),
                firstDayOfWeek = daysOfWeek().first()
            )
        }
    }

    private fun configureWeekCalendarView() {
        with (weekCalendarView) {
            dayBinder = getWeekDayBinder()
            weekHeaderBinder = getWeekHeaderBinder()
            weekScrollListener = object : WeekScrollListener {

                override fun invoke(week: Week) {
                    val year = week.days.last().date.yearMonth.year
                    viewModel.loadDataAdditional(Year.of(year))

                    viewModel.openWeek(week)
                }
            }

            setup(
                startDate = Constants.MIN_DATE,
                endDate = Constants.MAX_DATE,
                firstDayOfWeek = daysOfWeek().first()
            )
        }
    }

    private fun configureDayTitlesContainer(dayTitlesContainer: LinearLayout) {
        dayTitlesContainer.children.map { it as TextView }.forEachIndexed { index, textView ->
            val dayOfWeek = daysOfWeek()[index]
            val title = dayOfWeek.getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.getDefault())
            textView.text = title
        }
    }

    private fun configureCalendarDayView(binding: CalendarDayView, date: LocalDate, position: DayPosition? = null) {
        with (binding) {
            circleCount = viewModel.eventsLiveData.value?.get(date.toDate())?.size ?: 0

            alpha = if (position == DayPosition.MonthDate || position == null) 1f else 0.5f
            text = date.dayOfMonth.toString()
            contentColor = getContentColorForDay(
                date = date,
                selectedDate = viewModel.selectedDateLiveData.value!!,
                inMonth = position == DayPosition.MonthDate || position == null
            )
            background = getBackgroundForDay(
                date = date,
                selectedDate = viewModel.selectedDateLiveData.value!!
            )
        }
    }

    private fun changeCalendarViewWithAnimation(weekToMonth: Boolean)
    {
        if (animator != null) {
            animator?.cancel()
        }

        val selectedDate = viewModel.selectedDateLiveData.value?.toLocalDate() ?: return
        if (!weekToMonth) {
            weekCalendarView.scrollToDate(selectedDate)
        } else {
            monthCalendarView.scrollToDate(selectedDate)
        }

        val headerHeight = context.resources.getDimension(R.dimen.events_header_height).toInt()
        val dayHeight = context.resources.getDimension(R.dimen.calendar_day_size).toInt()

        val weekCalendarViewHeight = headerHeight + dayHeight
        val monthCalendarViewHeight = headerHeight + dayHeight * 6

        val oldHeight = if (weekToMonth) weekCalendarViewHeight else monthCalendarViewHeight
        val newHeight = if (weekToMonth) monthCalendarViewHeight else weekCalendarViewHeight

        animator = ValueAnimator.ofInt(oldHeight, newHeight)
        animator?.addUpdateListener { anim ->
            monthCalendarView.updateLayoutParams {
                height = anim.animatedValue as Int
            }
            monthCalendarView.children.forEach { child ->
                child.requestLayout()
            }
        }

        animator?.doOnStart {
            if (weekToMonth) {
                weekCalendarView.isVisible = false
                monthCalendarView.isVisible = true
            }
        }
        animator?.doOnEnd {
            if (!weekToMonth) {
                weekCalendarView.isVisible = true
                monthCalendarView.isVisible = false
            }
        }
        animator?.start()
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

    private fun getMonthHeaderText(month1: YearMonth, month2: YearMonth): String =
        if (month1 == month2)
            month1.formatToString()
        else if (month1.year == month2.year && month1.year == Year.now().value)
            context.getString(
                R.string.two_months_placeholder, month1.formatToString(), month2.formatToString()
            )
        else
            context.getString(
                R.string.two_months_placeholder,
                month1.formatToStringWithYear(shortMonth = true),
                month2.formatToStringWithYear(shortMonth = true)
            )

    private fun getMonthDayBinder(): MonthDayBinder<DayViewContainer> =
        object : MonthDayBinder<DayViewContainer> {

            override fun create(view: View) = DayViewContainer(view) {
                viewModel.selectDate(date = it.toDate())
            }

            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.date = data.date
                configureCalendarDayView(container.calendarDay, data.date, data.position)
            }
        }

    private fun getWeekDayBinder(): WeekDayBinder<DayViewContainer> =
        object : WeekDayBinder<DayViewContainer> {

            override fun create(view: View) = DayViewContainer(view) {
                viewModel.selectDate(date = it.toDate())
            }

            override fun bind(container: DayViewContainer, data: WeekDay) {
                container.date = data.date
                configureCalendarDayView(container.calendarDay, data.date)
            }
        }

    private fun getMonthHeaderBinder(): MonthHeaderFooterBinder<CalendarHeaderContainer> =
        object : MonthHeaderFooterBinder<CalendarHeaderContainer> {

            override fun create(view: View) = CalendarHeaderContainer(view)

            override fun bind(container: CalendarHeaderContainer, data: CalendarMonth) {
                with(container.binding) {
                    calendarMonthTextView.text = data.yearMonth.formatToString()
                    configureDayTitlesContainer(dayTitlesContainer)
                }
            }
        }

    private fun getWeekHeaderBinder(): WeekHeaderFooterBinder<CalendarHeaderContainer> =
        object : WeekHeaderFooterBinder<CalendarHeaderContainer> {

            override fun create(view: View) = CalendarHeaderContainer(view)

            override fun bind(container: CalendarHeaderContainer, data: Week) {
                with(container.binding) {
                    calendarMonthTextView.text = getMonthHeaderText(
                        month1 = data.days.first().date.yearMonth,
                        month2 = data.days.last().date.yearMonth
                    )
                    configureDayTitlesContainer(dayTitlesContainer)
                }
            }
        }
}