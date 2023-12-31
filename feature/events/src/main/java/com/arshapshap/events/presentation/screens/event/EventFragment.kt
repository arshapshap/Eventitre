package com.arshapshap.events.presentation.screens.event

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import com.arshapshap.common.domain.models.Event
import com.arshapshap.common_ui.base.BaseFragment
import com.arshapshap.common_ui.extensions.*
import com.arshapshap.common_ui.viewmodel.lazyViewModel
import com.arshapshap.events.R
import com.arshapshap.events.databinding.FragmentEventBinding
import com.arshapshap.events.di.EventsFeatureComponent
import com.arshapshap.events.di.EventsFeatureViewModel
import com.arshapshap.common.domain.Constants
import com.arshapshap.common.extensions.formatDateToString
import com.arshapshap.common.extensions.formatTimeToString
import com.arshapshap.common_ui.extensions.hideKeyboard
import com.arshapshap.common_ui.extensions.showAlertWithTwoButtons
import com.arshapshap.common_ui.extensions.showDatePickerDialog
import com.arshapshap.common_ui.extensions.showKeyboard
import com.arshapshap.common_ui.extensions.showTimePickerDialog
import com.arshapshap.common.extensions.toCalendar
import com.arshapshap.common.extensions.toDate
import java.util.*

class EventFragment : BaseFragment<FragmentEventBinding, EventViewModel>(
    FragmentEventBinding::inflate
) {

    companion object {

        fun createBundle(eventId: Long): Bundle {
            return bundleOf(EVENT_ID_KEY to eventId)
        }

        fun createBundle(date: Date): Bundle {
            return bundleOf(DATE_KEY to date)
        }

        private const val EVENT_ID_KEY = "EVENT_ID_KEY"
        private const val DATE_KEY = "DATE_KEY"
    }

    private val component by lazy {
        getFeatureComponent<EventsFeatureViewModel, EventsFeatureComponent>()
    }

    private var menuProvider: MenuProvider? = null

    @Suppress("DEPRECATION")
    override val viewModel: EventViewModel by lazyViewModel {
        component.eventViewModel().create(
            id = arguments?.getLong(EVENT_ID_KEY),
            date = arguments?.getSerializable(DATE_KEY) as? Date
        )
    }

    override fun inject() {
        component.inject(this)
    }

    override fun initViews() {
        with (binding) {
            nameEditText.doAfterTextChanged {
                viewModel.setName(it.toString())
            }
            descriptionEditText.doAfterTextChanged {
                viewModel.setDescription(it.toString())
            }
            deleteImageView.setOnClickListener {
                showAlertWithTwoButtons(
                    title = getString(R.string.confirm_deleting_event),
                    onPositiveButtonClick = {
                        viewModel.deleteEvent()
                        showToast(getString(R.string.event_deleted))
                    }
                )
            }
        }
    }

    override fun subscribe() {
        super.subscribe()
        with (viewModel) {
            eventLiveData.observe(viewLifecycleOwner) {
                with (binding) {
                    nameEditText.setText(it.name)
                    descriptionEditText.setText(it.description)
                    setDescriptionVisibility(it.description.isNotEmpty())
                    setFieldsEditing(false)
                    deleteImageView.isGone = viewModel.isCreating
                }
                setDateTimeFields(event = it)
                if (it != null)
                    requireActivity().setTitle(R.string.event_fragment_label)
            }
            editingEventLiveData.observe(viewLifecycleOwner) {
                setDateTimeFields(event = it)
            }
            isEditingLiveData.observe(viewLifecycleOwner) {
                setFieldsEditing(it)
                configureToolbar()
            }
            loadData()
        }
    }

    private fun configureToolbar() {
        val menuHost = requireActivity() as MenuHost
        menuHost.invalidateMenu()
        menuProvider?.let {
            menuHost.removeMenuProvider(it)
        }
        menuProvider = object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_toolbar_on_event_fragment, menu)
                menu.findItem(R.id.exportAction).isVisible = viewModel.isEditingLiveData.value == false
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.exportAction -> {
                        showAlertBeforeExport()
                        true
                    }
                    else -> false
                }
            }
        }
        menuProvider?.let {
            menuHost.addMenuProvider(it, viewLifecycleOwner, Lifecycle.State.RESUMED)
        }
    }

    private fun showAlertBeforeExport() {
        showAlertWithTwoButtons(
            title = getString(R.string.action_export),
            message = getString(R.string.export_event_confirm),
            onPositiveButtonClick = viewModel::exportEvent
        )
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

            if (viewModel.isCreating && isEditing) {
                nameEditText.requestFocus()
                showKeyboard(nameEditText)
            }

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
                    onDateSet = viewModel::setDateStart,
                    minDate = Constants.MIN_DATE.toDate().time,
                    maxDate = Constants.MAX_DATE.toDate().time
                )
            }
            setDateTimeFieldEditing(dateFinishTextView, isEditing) {
                showDatePickerDialog(
                    message = getString(R.string.event_finish_date),
                    getCurrent = ::getCurrentDateFinish,
                    onDateSet = viewModel::setDateFinish,
                    minDate = Constants.MIN_DATE.toDate().time,
                    maxDate = Constants.MAX_DATE.toDate().time
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
                if (isEditing)
                    viewModel.applyChanges()
                else
                    viewModel.editEvent()
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