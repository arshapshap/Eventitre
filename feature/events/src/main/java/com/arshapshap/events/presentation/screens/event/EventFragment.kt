package com.arshapshap.events.presentation.screens.event

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.arshapshap.common.base.BaseFragment
import com.arshapshap.common.base.ViewModelErrorLevel
import com.arshapshap.common.di.lazyViewModel
import com.arshapshap.common_ui.extensions.formatDayToString
import com.arshapshap.common_ui.extensions.showAlert
import com.arshapshap.common_ui.extensions.showAlertWithTwoButtons
import com.arshapshap.common_ui.extensions.showToast
import com.arshapshap.events.R
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
                dateStartTextView.text = it.dateStart.formatDayToString()
                dateFinishTextView.text = it.dateFinish.formatDayToString()
            }
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

    private fun setDescriptionVisibility(isVisible: Boolean) {
        with (binding) {
            descriptionHintTextView.isVisible = isVisible
            descriptionEditText.isVisible = isVisible
        }
    }

    private fun setFieldsEditing(isEditing: Boolean) {
        with (binding) {
            nameEditText.setEditing(isEditing)
            descriptionEditText.setEditing(isEditing)
            setDescriptionVisibility(isEditing || !descriptionEditText.text.isNullOrEmpty())

            setDateFieldEditing(dateStartTextView, isEditing)
            setDateFieldEditing(dateFinishTextView, isEditing)

            editImageView.setImageResource(if (isEditing) R.drawable.ic_done else R.drawable.ic_edit)
            editImageView.setOnClickListener {
                viewModel.setEditing(!isEditing)
                hideKeyboard()
            }
        }
    }

    private fun setDateFieldEditing(textView: TextView, isEditing: Boolean) {
        textView.setOnClickListener {
            if (!isEditing) return@setOnClickListener
        }
        textView.setBackgroundResource(if (isEditing) R.drawable.bg_clickable else android.R.color.transparent)
    }

    private fun EditText.setEditing(isEditing: Boolean) {
        isFocusable = isEditing
        isFocusableInTouchMode = isEditing
        setBackgroundResource(if (isEditing) R.drawable.bg_round_corners else android.R.color.transparent)
    }

    private fun hideKeyboard() {
        val imm = view?.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}