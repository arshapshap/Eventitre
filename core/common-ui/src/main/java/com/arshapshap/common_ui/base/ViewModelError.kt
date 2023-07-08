package com.arshapshap.common_ui.base

import androidx.annotation.StringRes

data class ViewModelError(
    @StringRes val messageRes: Int,
    val level: ViewModelErrorLevel
)

enum class ViewModelErrorLevel {
    Error,
    Message
}