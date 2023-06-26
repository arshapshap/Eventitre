package com.arshapshap.events.presentation.screens.calendar

import androidx.fragment.app.viewModels
import com.arshapshap.common.base.BaseFragment
import com.arshapshap.events.databinding.FragmentCalendarBinding
import com.arshapshap.events.di.EventsFeatureComponent
import com.arshapshap.events.di.EventsFeatureViewModel

class CalendarFragment : BaseFragment<FragmentCalendarBinding, CalendarViewModel>(
    FragmentCalendarBinding::inflate
) {

    override val viewModel: CalendarViewModel by viewModels()

    override fun inject() {
        getFeatureComponent<EventsFeatureViewModel, EventsFeatureComponent>().inject(this)
    }

    override fun initViews() {
        TODO("Not yet implemented")
    }

    override fun subscribe(viewModel: CalendarViewModel) {
        TODO("Not yet implemented")
    }
}