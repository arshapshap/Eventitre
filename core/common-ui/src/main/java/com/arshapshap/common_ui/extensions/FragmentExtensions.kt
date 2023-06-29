package com.arshapshap.common_ui.extensions

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.arshapshap.common_ui.R
import java.util.*

fun Fragment.showToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

fun Fragment.showToast(@StringRes message: Int) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
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

fun Fragment.showAlert(@StringRes title: Int, @StringRes message: Int, onClick: () -> Unit = { }) {
    AlertDialog.Builder(requireContext())
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(R.string.ok) { _, _ ->
            onClick.invoke()
        }
        .show()
}

fun Fragment.showAlertWithTwoButtons(
    @StringRes title: Int,
    @StringRes message: Int? = null,
    onPositiveButtonClick: () -> Unit = { },
    onNegativeButtonClick: () -> Unit = { }
) {
    var builder = AlertDialog.Builder(requireContext())
        .setTitle(title)
        .setPositiveButton(R.string.ok) { _, _ ->
            onPositiveButtonClick.invoke()
        }.setNegativeButton(R.string.cancel) { _, _ ->
            onNegativeButtonClick.invoke()
        }
    if (message != null)
        builder = builder.setMessage(message)
    builder.show()
}

fun Fragment.showTimePickerDialog(title: String = "", message: String = "", getCurrent: () -> Calendar, onTimeSet: (Calendar) -> Unit) {
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

fun Fragment.showDatePickerDialog(title: String = "", message: String = "", getCurrent: () -> Calendar, onDateSet: (Calendar) -> Unit) {
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
    dialog.show()
}

fun Fragment.hideKeyboard() {
    val imm = view?.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view?.windowToken, 0)
}