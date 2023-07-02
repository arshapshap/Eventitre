package com.arshapshap.settings.di

import com.arshapshap.common.di.base.BaseFeatureComponent
import com.arshapshap.common.di.scopes.FeatureScope
import com.arshapshap.settings.di.modules.SettingsFeatureModule
import com.arshapshap.settings.presentation.screens.settings.SettingsFragment
import com.arshapshap.settings.presentation.screens.settings.SettingsViewModel
import dagger.Component

@Component(
    modules = [SettingsFeatureModule::class],
    dependencies = [SettingsFeatureDependencies::class]
)
@FeatureScope
internal interface SettingsFeatureComponent : BaseFeatureComponent {

    @Component.Builder
    interface Builder {

        fun withDependencies(dependencies: SettingsFeatureDependencies): Builder

        fun build(): SettingsFeatureComponent
    }

    fun inject(fragment: SettingsFragment)
    fun settingsViewModel(): SettingsViewModel.Factory
}