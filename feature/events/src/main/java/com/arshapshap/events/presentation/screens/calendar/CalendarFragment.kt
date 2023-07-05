package com.arshapshap.events.presentation.screens.calendar

import android.widget.ImageButton
import androidx.core.view.isVisible
import com.arshapshap.common_ui.base.BaseFragment
import com.arshapshap.common_ui.extensions.isSameDay
import com.arshapshap.common_ui.extensions.toDate
import com.arshapshap.common_ui.viewmodel.lazyViewModel
import com.arshapshap.events.databinding.FragmentCalendarBinding
import com.arshapshap.events.di.EventsFeatureComponent
import com.arshapshap.events.di.EventsFeatureViewModel
import com.arshapshap.events.presentation.screens.calendar.calendarview.CalendarManager
import java.time.LocalDate

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

    private var _calendarManager: CalendarManager? = null
    private val calendarManager: CalendarManager
        get() = _calendarManager!!

    private var firstOpening = true

    override fun initViews() {
        firstOpening = true
        with (binding) {
            todayTextView.text = LocalDate.now().dayOfMonth.toString()
            eventsRecyclerView.adapter = EventsRecyclerViewAdapter {
                viewModel.openEvent(it)
            }
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
            }
            eventsLiveData.observe(viewLifecycleOwner) {
                it.keys.forEach {
                    calendarManager.notifyDateChanged(it)
                }
            }
            changedLiveData.observe(viewLifecycleOwner) {
                it.forEach {
                    calendarManager.notifyDateChanged(it)
                }
            }
            listLiveData.observe(viewLifecycleOwner) {
                getEventsRecyclerViewAdapter().setList(it)
            }
            unselectedDateLiveData.observe(viewLifecycleOwner) {
                calendarManager.notifyDateChanged(it)
            }
            selectedDateLiveData.observe(viewLifecycleOwner) {
                calendarManager.notifyDateChanged(it)
                if (firstOpening) {
                    firstOpening = false
                    calendarManager.scrollToDate(it)
                }
                else {
                    calendarManager.smoothScrollToDate(it)
                }

                animateShowTodayButton(!it.isSameDay(LocalDate.now().toDate()))
            }
            isCalendarExpandedLiveData.observe(viewLifecycleOwner) {
                calendarManager.isCalendarExpanded = it
                binding.changeCalendarViewButton.rotate(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _calendarManager = null
        firstOpening = true
    }

    private fun getEventsRecyclerViewAdapter(): EventsRecyclerViewAdapter =
        binding.eventsRecyclerView.adapter as EventsRecyclerViewAdapter

    private fun ImageButton.rotate(isExpanded: Boolean) {
        val angle = if (isExpanded) 0F else 180F
        this.animate().rotation(angle).start()
    }

    private fun animateShowTodayButton(isToday: Boolean) {
        binding.showTodayButton.isVisible = !isToday
        binding.todayTextView.isVisible = !isToday
    }
}