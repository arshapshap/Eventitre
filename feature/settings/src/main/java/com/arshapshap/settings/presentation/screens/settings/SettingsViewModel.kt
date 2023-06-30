package com.arshapshap.settings.presentation.screens.settings

import com.arshapshap.common_ui.base.BaseViewModel
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class SettingsViewModel @AssistedInject constructor() : BaseViewModel() {

    @AssistedFactory
    interface Factory {

        fun create(): SettingsViewModel
    }
}