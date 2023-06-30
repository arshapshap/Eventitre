package com.arshapshap.events.presentation.screens.event

import android.widget.EditText
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.arshapshap.common_ui.base.BaseFragment
import com.arshapshap.common_ui.base.ViewModelErrorLevel
import com.arshapshap.common_ui.viewmodel.lazyViewModel
import com.arshapshap.common_ui.extensions.*
import com.arshapshap.events.R
import com.arshapshap.events.databinding.FragmentEventBinding
import com.arshapshap.events.di.EventsFeatureComponent
import com.arshapshap.events.di.EventsFeatureViewModel
import com.arshapshap.events.domain.models.Event
import java.util.*

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
            arguments?.getLong(EVENT_ID_KEY) ?: throw IllegalArgumentException("Event not found")
        )
    }

    override fun inject() {
        component.inject(this)
    }

    override fun initViews() {
        with (binding) {
            setFieldsEditing(false)
            nameEditText.doAfterTextChanged {
                viewModel.setName(it.toString())
            }
            descriptionEditText.doAfterTextChanged {
                viewModel.setDescription(it.toString())
            }
            deleteImageView.setOnClickListener {
                showAlertWithTwoButtons(
                    title = R.string.confirm_deleting_event,
                    onPositiveButtonClick = viewModel::deleteEvent
                )
            }
        }
    }

    override fun subscribe() {
        viewModel.loadingLiveData.observe(viewLifecycleOwner) {
            binding.root.isVisible = !it
        }
        viewModel.eventLiveData.observe(viewLifecycleOwner) {
            with (binding) {
                nameEditText.setText(it.name)
                descriptionEditText.setText(it.description)
                setDescriptionVisibility(it.description.isNotEmpty())
            }
            setDateTimeFields(event = it)
        }
        viewModel.editingEventLiveData.observe(viewLifecycleOwner) {
            setDateTimeFields(event = it)
        }
        viewModel.isEditingLiveData.observe(viewLifecycleOwner) {
            setFieldsEditing(it)
        }
        viewModel.errorLiveData.observe(viewLifecycleOwner) {
            when (it.level) {
                ViewModelErrorLevel.Error ->
                    showAlert(
                        title = com.arshapshap.common_ui.R.string.error,
                        message = it.messageRes
                    )
                ViewModelErrorLevel.Warn ->
                    showToast(message = it.messageRes)
            }
        }
        viewModel.loadData()
    }

    private fun setDateTimeFields(event: Event?) {
        with (binding) {
            timeStartTextView.text = event?.dateStart?.formatTimeToString()
            timeFinishTextView.text = event?.dateFinish?.formatTimeToString()
            dateStartTextView.text = event?.dateStart?.formatDateToString()
            dateFinishTextView.text = event?.dateFinish?.formatDateToString()
        }
    }

    private fun setDescriptionVisibility(isVisible: Boolean) {
        with (binding) {
            descriptionHintTextView.text = if (isVisible) getString(R.string.description) else ""
            descriptionEditText.isVisible = isVisible
        }
    }

    private fun setFieldsEditing(isEditing: Boolean) {
        with (binding) {
            setInputFieldEditing(nameEditText, isEditing)
            setInputFieldEditing(descriptionEditText, isEditing)
            setDescriptionVisibility(isEditing || !descriptionEditText.text.isNullOrEmpty())

            setDateTimeFieldEditing(timeStartTextView, isEditing) {
                showTimePickerDialog(
                    message = getString(R.string.event_start_time),
                    getCurrent = ::getCurrentDateStart,
                    onTimeSet = viewModel::setDateStart
                )
            }
            setDateTimeFieldEditing(timeFinishTextView, isEditing) {
                showTimePickerDialog(
                    message = getString(R.string.event_finish_time),
                    getCurrent = ::getCurrentDateFinish,
                    onTimeSet = viewModel::setDateFinish
                )
            }
            setDateTimeFieldEditing(dateStartTextView, isEditing) {
                showDatePickerDialog(
                    message = getString(R.string.event_start_date),
                    getCurrent = ::getCurrentDateStart,
                    onDateSet = viewModel::setDateStart
                )
            }
            setDateTimeFieldEditing(dateFinishTextView, isEditing) {
                showDatePickerDialog(
                    message = getString(R.string.event_finish_date),
                    getCurrent = ::getCurrentDateFinish,
                    onDateSet = viewModel::setDateFinish
                )
            }

            changeEditImageView(isEditing)
        }
    }

    private fun getCurrentDateStart(): Calendar {
        return viewModel.editingEventLiveData.value?.dateStart?.toCalendar() ?: Calendar.getInstance()
    }

    private fun getCurrentDateFinish(): Calendar {
        return viewModel.editingEventLiveData.value?.dateFinish?.toCalendar() ?: Calendar.getInstance()
    }

    private fun changeEditImageView(isEditing: Boolean) {
        with (binding) {
            editImageView.setImageResource(if (isEditing) R.drawable.ic_done else R.drawable.ic_edit)
            editImageView.setOnClickListener {
                viewModel.setEditing(!isEditing)
                hideKeyboard()
            }
        }
    }

    private fun setDateTimeFieldEditing(textView: TextView, isEditing: Boolean, onClickListener: () -> Unit) {
        textView.setOnClickListener {
            if (!isEditing) return@setOnClickListener
            onClickListener.invoke()
        }
        textView.setBackgroundResource(if (isEditing) R.drawable.bg_clickable_with_stroke else android.R.color.transparent)
    }

    private fun setInputFieldEditing(editText: EditText, isEditing: Boolean) {
        editText.isFocusable = isEditing
        editText.isFocusableInTouchMode = isEditing
        editText.setBackgroundResource(if (isEditing) R.drawable.bg_round_corners else android.R.color.transparent)
    }
}