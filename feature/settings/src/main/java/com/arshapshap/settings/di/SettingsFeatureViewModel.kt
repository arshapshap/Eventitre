package com.arshapshap.settings.di

import com.arshapshap.common.di.base.BaseFeatureViewModel

internal class SettingsFeatureViewModel : BaseFeatureViewModel<SettingsFeatureComponent>() {
    override val component = DaggerSettingsFeatureComponent.builder()
        .withDependencies(SettingsFeatureDependenciesStore.dependencies)
        .build()
}