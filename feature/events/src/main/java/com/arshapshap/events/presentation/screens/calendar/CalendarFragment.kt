package com.arshapshap.events.presentation.screens.calendar

import android.view.View
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.children
import com.arshapshap.common_ui.base.BaseFragment
import com.arshapshap.common_ui.extensions.*
import com.arshapshap.common_ui.viewmodel.lazyViewModel
import com.arshapshap.events.R
import com.arshapshap.events.databinding.FragmentCalendarBinding
import com.arshapshap.events.di.EventsFeatureComponent
import com.arshapshap.events.di.EventsFeatureViewModel
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
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
            loadData()
            listLiveData.observe(viewLifecycleOwner) {
                getEventsRecyclerViewAdapter().setList(it)
            }
            unselectedDateLiveData.observe(viewLifecycleOwner) {
                binding.calendarView.notifyDateChanged(it.toLocalDate())
            }
            selectedDateLiveData.observe(viewLifecycleOwner) {
                binding.calendarView.notifyDateChanged(it.toLocalDate())
                binding.calendarView.smoothScrollToDate(it.toLocalDate())
            }
        }
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
                    with (container.binding) {
                        calendarDayTextView.text = data.date.dayOfMonth.toString()
                        calendarDayTextView.alpha = if (data.position == DayPosition.MonthDate) 1f else 0.5f
                        calendarDayTextView.setTextColor(
                            if (data.date == LocalDate.now()) getColorFromTheme(
                                com.google.android.material.R.attr.colorSecondary
                            ) else getColorFromTheme(
                                com.google.android.material.R.attr.colorOnBackground
                            )
                        )
                        frameLayout.background =
                            if (viewModel.selectedDateLiveData.value?.isSameDay(data.date.toDate()) == true)
                                AppCompatResources.getDrawable(requireContext(), R.drawable.bg_circle)
                            else null
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
            val currentMonth = YearMonth.now()
            val startMonth = currentMonth.minusMonths(100)
            val endMonth = currentMonth.plusMonths(100)
            val firstDayOfWeek = daysOfWeek().first()

            calendarView.setup(startMonth, endMonth, firstDayOfWeek)
            calendarView.scrollToMonth(currentMonth)
        }
    }
}