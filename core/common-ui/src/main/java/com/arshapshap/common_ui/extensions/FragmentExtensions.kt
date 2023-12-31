package com.arshapshap.common_ui.extensions

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.util.TypedValue
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.arshapshap.common_ui.R
import java.util.*

@ColorInt
fun Context.getColorAttributeFromTheme(@AttrRes attr: Int): Int {
    val typedValue = TypedValue()
    theme?.resolveAttribute(attr, typedValue, true)
    return typedValue.data
}

fun Fragment.showToast(message: String, longLength: Boolean = false) {
    Toast.makeText(requireContext(), message, if (longLength) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
}

fun Fragment.showAlert(title: String, message: String, onClick: () -> Unit = { }) {
    AlertDialog.Builder(requireContext())
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(R.string.ok) { _, _ ->
            onClick.invoke()
        }
        .show()
}

fun Fragment.showAlertWithTwoButtons(
    title: String,
    message: String? = null,
    positiveButtonText: String = getString(R.string.ok),
    negativeButtonText: String = getString(R.string.cancel),
    onPositiveButtonClick: () -> Unit = { },
    onNegativeButtonClick: () -> Unit = { }
) {
    var builder = AlertDialog.Builder(requireContext())
        .setTitle(title)
        .setPositiveButton(positiveButtonText) { _, _ ->
            onPositiveButtonClick.invoke()
        }.setNegativeButton(negativeButtonText) { _, _ ->
            onNegativeButtonClick.invoke()
        }
    if (message != null)
        builder = builder.setMessage(message)
    builder.show()
}

fun Fragment.showAlertWithThreeButtons(
    title: String,
    neutralButtonText: String,
    message: String? = null,
    positiveButtonText: String = getString(R.string.ok),
    negativeButtonText: String = getString(R.string.cancel),
    onPositiveButtonClick: () -> Unit = { },
    onNeutralButtonClick: () -> Unit = { },
    onNegativeButtonClick: () -> Unit = { }
) {
    // The neutral and negative buttons had to be replaced in order to position the neutral button in the center
    var builder = AlertDialog.Builder(requireContext())
        .setTitle(title)
        .setPositiveButton(positiveButtonText) { _, _ ->
            onPositiveButtonClick.invoke()
        }.setNegativeButton(neutralButtonText) { _, _ ->
            onNeutralButtonClick.invoke()
        }.setNeutralButton(negativeButtonText) { _, _ ->
            onNegativeButtonClick.invoke()
        }
    if (message != null)
        builder = builder.setMessage(message)
    builder.show()
}

fun Fragment.showTimePickerDialog(
    title: String = "",
    message: String = "",
    getCurrent: () -> Calendar,
    onTimeSet: (Calendar) -> Unit
) {
    val current = getCurrent()
    val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
        current.set(Calendar.HOUR_OF_DAY, hour)
        current.set(Calendar.MINUTE, minute)
        onTimeSet.invoke(current)
    }
     val dialog = TimePickerDialog(
        requireContext(),
        timeSetListener,
        current.get(Calendar.HOUR_OF_DAY),
        current.get(Calendar.MINUTE),
        true
    )
    if (title.isNotEmpty())
        dialog.setTitle(title)
    if (message.isNotEmpty())
        dialog.setMessage(message)
    dialog.show()
}

fun Fragment.showDatePickerDialog(
    title: String = "",
    message: String = "",
    getCurrent: () -> Calendar,
    onDateSet: (Calendar) -> Unit,
    minDate: Long? = null,
    maxDate: Long? = null
) {
    val current = getCurrent()
    val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
        current.set(Calendar.YEAR, year)
        current.set(Calendar.MONTH, month)
        current.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        onDateSet.invoke(current)
    }
    val dialog = DatePickerDialog(
        requireContext(),
        dateSetListener,
        current.get(Calendar.YEAR),
        current.get(Calendar.MONTH),
        current.get(Calendar.DAY_OF_MONTH)
    )
    if (title.isNotEmpty())
        dialog.setTitle(title)
    if (message.isNotEmpty())
        dialog.setMessage(message)
    minDate?.let {
        dialog.datePicker.minDate = it
    }
    maxDate?.let {
        dialog.datePicker.maxDate = it
    }
    dialog.show()
}

fun Fragment.hideKeyboard() {
    val imm = view?.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view?.windowToken, 0)
}

fun Fragment.showKeyboard(editText: EditText) {
    val imm = view?.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
}