package com.arshapshap.events.presentation.screens.event

import com.arshapshap.common.base.BaseFragment
import com.arshapshap.common.di.lazyViewModel
import com.arshapshap.common.extensions.formatDayToString
import com.arshapshap.events.databinding.FragmentEventBinding
import com.arshapshap.events.di.EventsFeatureComponent
import com.arshapshap.events.di.EventsFeatureViewModel

class EventFragment : BaseFragment<FragmentEventBinding, EventViewModel>(
    FragmentEventBinding::inflate
) {

    companion object {

        const val EVENT_ID_KEY = "EVENT_ID_KEY"
    }

    private val component by lazy {
        getFeatureComponent<EventsFeatureViewModel, EventsFeatureComponent>()
    }

    override val viewModel: EventViewModel by lazyViewModel {
        component.eventViewModel().create(
            arguments?.getInt(EVENT_ID_KEY)
        )
    }

    override fun inject() {
        component.inject(this)
    }

    override fun initViews() {
    }

    override fun subscribe() {
        viewModel.loadData()
        viewModel.eventLiveData.observe(viewLifecycleOwner) {
            with(binding) {
                nameTextView.text = it.name
                descriptionTextView.text = it.description
                dateStartTextView.text = it.dateStart.formatDayToString()
                dateFinishTextView.text = it.dateFinish.formatDayToString()
            }
        }
    }
}