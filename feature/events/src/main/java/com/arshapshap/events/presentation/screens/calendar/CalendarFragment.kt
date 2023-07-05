package com.arshapshap.events.presentation.screens.calendar

import androidx.core.view.isVisible
import com.arshapshap.common_ui.base.BaseFragment
import com.arshapshap.common_ui.extensions.toLocalDate
import com.arshapshap.common_ui.viewmodel.lazyViewModel
import com.arshapshap.events.databinding.FragmentCalendarBinding
import com.arshapshap.events.di.EventsFeatureComponent
import com.arshapshap.events.di.EventsFeatureViewModel
import com.arshapshap.events.presentation.screens.calendar.calendarview.CalendarManager

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

    override fun initViews() {
        with (binding) {
            eventsRecyclerView.adapter = EventsRecyclerViewAdapter {
                viewModel.openEvent(it)
            }
            addButton.setOnClickListener {
                viewModel.openEventCreating()
            }

            _calendarManager = CalendarManager(requireContext(), viewModel, calendarView, weekCalendarView)
            calendarManager.isMonthCalendarOnScreen = false
            calendarManager.setup()
        }
    }

    private var firstOpenning = true

    override fun subscribe() {
        super.subscribe()
        with (viewModel) {
            loadingLiveData.observe(viewLifecycleOwner) {
                binding.loadingProgressBar.isVisible = it
            }
            eventsLiveData.observe(viewLifecycleOwner) {
                it.keys.forEach {
                    calendarManager.notifyDateChanged(it.toLocalDate())
                }
            }
            changedLiveData.observe(viewLifecycleOwner) {
                it.forEach {
                    calendarManager.notifyDateChanged(it.toLocalDate())
                }
            }
            listLiveData.observe(viewLifecycleOwner) {
                getEventsRecyclerViewAdapter().setList(it)
            }
            unselectedDateLiveData.observe(viewLifecycleOwner) {
                calendarManager.notifyDateChanged(it.toLocalDate())
            }
            selectedDateLiveData.observe(viewLifecycleOwner) {
                calendarManager.notifyDateChanged(it.toLocalDate())
                if (firstOpenning) {
                    firstOpenning = false
                    calendarManager.scrollToDate(it.toLocalDate())
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _calendarManager = null
    }

    private fun getEventsRecyclerViewAdapter(): EventsRecyclerViewAdapter =
        binding.eventsRecyclerView.adapter as EventsRecyclerViewAdapter
}