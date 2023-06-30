package com.arshapshap.settings.presentation.screens.settings

import com.arshapshap.common_ui.base.BaseFragment
import com.arshapshap.common_ui.viewmodel.lazyViewModel
import com.arshapshap.settings.databinding.FragmentSettingsBinding
import com.arshapshap.settings.di.SettingsFeatureComponent
import com.arshapshap.settings.di.SettingsFeatureViewModel

class SettingsFragment : BaseFragment<FragmentSettingsBinding, SettingsViewModel>(
    FragmentSettingsBinding::inflate
) {

    private val component by lazy {
        getFeatureComponent<SettingsFeatureViewModel, SettingsFeatureComponent>()
    }

    override val viewModel: SettingsViewModel by lazyViewModel {
        component.settingsViewModel().create()
    }

    override fun inject() {
        component.inject(this)
    }

    override fun initViews() {
        //TODO("Not yet implemented")
    }

    override fun subscribe() {
        //TODO("Not yet implemented")
    }
}