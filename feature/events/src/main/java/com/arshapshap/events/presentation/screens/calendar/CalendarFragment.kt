package com.arshapshap.events.presentation.screens.calendar

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import com.arshapshap.common.domain.models.Event
import com.arshapshap.common_ui.base.BaseFragment
import com.arshapshap.common.extensions.isSameDay
import com.arshapshap.common.extensions.toCalendar
import com.arshapshap.common.extensions.toDate
import com.arshapshap.common_ui.extensions.showDatePickerDialog
import com.arshapshap.common_ui.viewmodel.lazyViewModel
import com.arshapshap.events.R
import com.arshapshap.events.databinding.FragmentCalendarBinding
import com.arshapshap.events.di.EventsFeatureComponent
import com.arshapshap.events.di.EventsFeatureViewModel
import com.arshapshap.events.presentation.screens.calendar.calendarview.CalendarManager
import com.arshapshap.events.presentation.screens.calendar.timelinelayout.EventTimelineModel
import com.arshapshap.events.presentation.screens.calendar.timelinelayout.EventView
import java.time.LocalDate
import java.util.Calendar
import com.arshapshap.common.domain.Constants
import com.arshapshap.common.extensions.getMonthDifference
import java.util.Date

class CalendarFragment : BaseFragment<FragmentCalendarBinding, CalendarViewModel>(
    FragmentCalendarBinding::inflate
), MenuProvider {

    private val component by lazy {
        getFeatureComponent<EventsFeatureViewModel, EventsFeatureComponent>()
    }

    override val viewModel: CalendarViewModel by lazyViewModel {
        component.calendarViewModel().create()
    }

    override fun inject() {
        component.inject(this)
    }

    private var _calendarManager: CalendarManager? = null
    private val calendarManager: CalendarManager
        get() = _calendarManager!!

    private var firstCalendarScroll = true
    private var firstCalendarExpanding = true

    override fun initViews() {
        firstCalendarScroll = true
        firstCalendarExpanding = true

        requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        with (binding) {
            todayTextView.text = LocalDate.now().dayOfMonth.toString()
            showTodayButton.setOnClickListener {
                viewModel.selectDate(LocalDate.now().toDate())
            }
            addButton.setOnClickListener {
                viewModel.openEventCreating()
            }
            changeCalendarViewButton.setOnClickListener {
                viewModel.changeCalendarView()
            }

            _calendarManager = CalendarManager(requireContext(), viewModel, monthCalendarView, weekCalendarView)
            calendarManager.setup()
        }
    }

    override fun subscribe() {
        super.subscribe()
        with (viewModel) {
            loadDataInitial()
            loadingLiveData.observe(viewLifecycleOwner) {
                binding.loadingProgressBar.isVisible = it
                binding.eventsTimeline.isVisible = !it
            }
            changedDatesLiveData.observe(viewLifecycleOwner) {
                it.forEach {
                    calendarManager.notifyDateChanged(it)
                }
            }
            listLiveData.observe(viewLifecycleOwner) {
                binding.eventsTimeline.clearEvents()
                it.getContentIfNotHandled()?.let { list ->
                    list.forEach { event ->
                        addEventToTimeline(event)
                    }
                }
            }
            selectedDateLiveData.observe(viewLifecycleOwner) {
                calendarManager.notifyDateChanged(it.oldDate)
                calendarManager.notifyDateChanged(it.newDate)
                if (firstCalendarScroll || !needsToAnimateTransitionToDate(it.oldDate, it.newDate)) {
                    firstCalendarScroll = false
                    calendarManager.scrollToDate(it.newDate)
                } else {
                    calendarManager.smoothScrollToDate(it.newDate)
                }

                animateShowTodayButton(it.newDate.isSameDay(LocalDate.now().toDate()))
            }
            isCalendarExpandedLiveData.observe(viewLifecycleOwner) {
                if (firstCalendarExpanding) {
                    firstCalendarExpanding = false
                    calendarManager.changeCalendarView(it)
                } else {
                    calendarManager.changeCalendarViewWithAnimation(it)
                }
                binding.changeCalendarViewButton.rotate(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _calendarManager = null
    }

    private fun addEventToTimeline(event: Event) {
        val model = EventTimelineModel(event, viewModel.selectedDate)
        val eventView = EventView(requireContext(),
            model,
            layoutResourceId = R.layout.item_event_on_timeline,
            setupView = { view ->
                view.findViewById<TextView>(R.id.name_text_view).text = model.title
                view.findViewById<TextView>(R.id.time_start_text_view).text = model.startTimeString
                view.findViewById<TextView>(R.id.time_finish_text_view).text = model.endTimeString
            },
            onItemClick = {
                viewModel.openEvent(event)
            }
        )

        binding.eventsTimeline.addEvent(eventView)
    }

    private fun ImageButton.rotate(isExpanded: Boolean) {
        val angle = if (isExpanded) 0F else 180F
        this.animate().rotation(angle).start()
    }

    private fun animateShowTodayButton(isToday: Boolean) {
        binding.showTodayButton.isVisible = !isToday
        binding.todayTextView.isVisible = !isToday
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_toolbar_on_calendar_fragment, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.goToDateAction -> {
                showDatePickerDialog(
                    title = getString(R.string.action_go_to_date),
                    getCurrent = ::getCurrentDate,
                    onDateSet = ::goToDate,
                    minDate = Constants.MIN_DATE.toDate().time,
                    maxDate = Constants.MAX_DATE.toDate().time
                )
                true
            }
            else -> false
        }
    }

    private fun needsToAnimateTransitionToDate(oldDate: Date, newDate: Date): Boolean {
        return (viewModel.isCalendarExpandedLiveData.value!! && getMonthDifference(oldDate, newDate) < 6) || (getMonthDifference(oldDate, newDate) < 3)
    }

    private fun getCurrentDate(): Calendar {
        return viewModel.selectedDate.toCalendar()
    }

    private fun goToDate(calendar: Calendar) {
        viewModel.selectDate(calendar.time)
    }
}