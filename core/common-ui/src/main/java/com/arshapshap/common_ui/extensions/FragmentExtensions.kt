package com.arshapshap.common_ui.extensions

import android.app.AlertDialog
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.arshapshap.common_ui.R

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