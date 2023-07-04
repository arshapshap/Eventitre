package com.arshapshap.events.presentation.screens.calendar

import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.children
import com.arshapshap.common_ui.base.BaseFragment
import com.arshapshap.common_ui.extensions.*
import com.arshapshap.common_ui.viewmodel.lazyViewModel
import com.arshapshap.events.R
import com.arshapshap.events.databinding.FragmentCalendarBinding
import com.arshapshap.events.di.EventsFeatureComponent
import com.arshapshap.events.di.EventsFeatureViewModel
import com.arshapshap.events.presentation.screens.calendar.calendarview.DayViewContainer
import com.arshapshap.events.presentation.screens.calendar.calendarview.MonthViewContainer
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.view.CalendarView
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.MonthScrollListener
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

class CalendarFragment : BaseFragment<FragmentCalendarBinding, CalendarViewModel>(
    FragmentCalendarBinding::inflate
) {

    private val component by lazy {
        getFeatureComponent<EventsFeatureViewModel, EventsFeatureComponent>()
    }

    override val viewModel: CalendarViewModel by lazyViewModel {
        component.calendarViewModel().create()
    }

    override fun inject() {
        component.inject(this)
    }

    override fun initViews() {
        with (binding) {
            eventsRecyclerView.adapter = EventsRecyclerViewAdapter {
                viewModel.openEvent(it)
            }
            addButton.setOnClickListener {
                viewModel.openEventCreating()
            }
            configureCalendarView()
        }
    }

    override fun subscribe() {
        super.subscribe()
        with (viewModel) {
            eventsLiveData.observe(viewLifecycleOwner) {
                it.keys.forEach {
                    binding.calendarView.notifyDateChangedEverywhere(it.toLocalDate())
                }
            }
            listLiveData.observe(viewLifecycleOwner) {
                getEventsRecyclerViewAdapter().setList(it)
            }
            unselectedDateLiveData.observe(viewLifecycleOwner) {
                binding.calendarView.notifyDateChangedEverywhere(it.toLocalDate())
            }
            selectedDateLiveData.observe(viewLifecycleOwner) {
                binding.calendarView.notifyDateChangedEverywhere(it.toLocalDate())
                binding.calendarView.scrollToDate(it.toLocalDate())
            }
        }
    }

    private fun loadData() {
        val dateStart = binding.calendarView.findFirstVisibleDay()?.date?.toDate()?.addMonths(-1) ?: return
        val dateFinish = binding.calendarView.findLastVisibleDay()?.date?.toDate()?.addMonths(1) ?: return
        viewModel.loadData(
            dateStart = dateStart,
            dateFinish = dateFinish
        )
    }

    private fun getEventsRecyclerViewAdapter(): EventsRecyclerViewAdapter =
        binding.eventsRecyclerView.adapter as EventsRecyclerViewAdapter

    private fun configureCalendarView() {
        with (binding) {
            calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {

                override fun create(view: View) = DayViewContainer(view) {
                    viewModel.selectDate(date = it.toDate())
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
                            position = data.position
                        )
                        background = getBackgroundForDay(
                            date = data.date,
                            selectedDate = viewModel.selectedDateLiveData.value!!
                        )
                    }
                }
            }

            calendarView.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {

                override fun create(view: View) = MonthViewContainer(view)

                override fun bind(container: MonthViewContainer, data: CalendarMonth) {
                    with (container.binding) {
                        calendarMonthTextView.text = data.yearMonth.formatToString()
                        dayTitlesContainer.children.map { it as TextView }.forEachIndexed { index, textView ->
                            val dayOfWeek = daysOfWeek()[index]
                            val title = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                            textView.text = title
                        }
                    }
                }
            }

            calendarView.monthScrollListener = object : MonthScrollListener {
                override fun invoke(p1: CalendarMonth) {
                    loadData()
                }
            }

            val startMonth = YearMonth.of(1970, 1)
            val endMonth = YearMonth.of(2099, 12)

            calendarView.setup(startMonth, endMonth, daysOfWeek().first())
            calendarView.scrollToMonth(YearMonth.now())
        }
    }

    private fun CalendarView.notifyDateChangedEverywhere(localDate: LocalDate) {
        this.notifyDateChanged(localDate, DayPosition.InDate)
        this.notifyDateChanged(localDate, DayPosition.MonthDate)
        this.notifyDateChanged(localDate, DayPosition.OutDate)
    }

    @Suppress("DEPRECATION")
    @ColorInt
    private fun getContentColorForDay(date: LocalDate, selectedDate: Date, position: DayPosition) =
        if (selectedDate.isSameDay(date.toDate()) && date == LocalDate.now() && position == DayPosition.MonthDate)
            getColorAttributeFromTheme(com.google.android.material.R.attr.colorOnSecondary)
        else if (date == LocalDate.now() && position == DayPosition.MonthDate)
            getColorAttributeFromTheme(com.google.android.material.R.attr.colorSecondary)
        else if (date.dayOfWeek == DayOfWeek.SUNDAY && position == DayPosition.MonthDate)
            resources.getColor(com.arshapshap.common_ui.R.color.red)
        else
            getColorAttributeFromTheme(com.google.android.material.R.attr.colorOnBackground)


    private fun getBackgroundForDay(date: LocalDate, selectedDate: Date) =
        if (selectedDate.isSameDay(date.toDate()) && date == LocalDate.now())
            AppCompatResources.getDrawable(requireContext(), R.drawable.bg_circle_filled)
        else if (selectedDate.isSameDay(date.toDate()))
            AppCompatResources.getDrawable(requireContext(), R.drawable.bg_circle)
        else null
}