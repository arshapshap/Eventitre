package com.arshapshap.events.presentation.screens.event

import android.text.method.ScrollingMovementMethod
import androidx.core.view.isVisible
import com.arshapshap.common.base.BaseFragment
import com.arshapshap.common.di.lazyViewModel
import com.arshapshap.common_ui.extensions.formatDayToString
import com.arshapshap.common_ui.extensions.showAlert
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
            arguments?.getInt(EVENT_ID_KEY) ?: throw IllegalArgumentException("Event not found")
        )
    }

    override fun inject() {
        component.inject(this)
    }

    override fun initViews() {
        with (binding) {
            descriptionTextView.movementMethod = ScrollingMovementMethod()
        }
    }

    override fun subscribe() {
        viewModel.loadData()
        viewModel.eventLiveData.observe(viewLifecycleOwner) {
            with(binding) {
                nameTextView.setText(it.name)
                descriptionTextView.setText(it.description)
                descriptionHintTextView.isVisible = it.description.isNotEmpty()
                dateStartTextView.text = it.dateStart.formatDayToString()
                dateFinishTextView.text = it.dateFinish.formatDayToString()
            }
        }
        viewModel.errorFromResourceLiveData.observe(viewLifecycleOwner) {
            showAlert(
                title = com.arshapshap.common_ui.R.string.error,
                message = it
            ) {
                viewModel.closeFragment()
            }
        }
    }
}