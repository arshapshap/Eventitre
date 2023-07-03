package com.arshapshap.events.presentation.screens.calendar

import com.arshapshap.common_ui.base.BaseFragment
import com.arshapshap.common_ui.extensions.createDate
import com.arshapshap.common_ui.viewmodel.lazyViewModel
import com.arshapshap.events.databinding.FragmentCalendarBinding
import com.arshapshap.events.di.EventsFeatureComponent
import com.arshapshap.events.di.EventsFeatureViewModel

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
            calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
                viewModel.loadEventsByDate(createDate(dayOfMonth, month, year))
            }
        }
    }

    override fun subscribe() {
        super.subscribe()
        with (viewModel) {
            loadData()
            listLiveData.observe(viewLifecycleOwner) {
                getEventsRecyclerViewAdapter().setList(it)
            }
            selectedDateLiveData.observe(viewLifecycleOwner) {
                with (binding.calendarView) {
                    if (this.date != it.time)
                        this.date = it.time
                }
            }
        }
    }

    private fun getEventsRecyclerViewAdapter(): EventsRecyclerViewAdapter =
        binding.eventsRecyclerView.adapter as EventsRecyclerViewAdapter
}