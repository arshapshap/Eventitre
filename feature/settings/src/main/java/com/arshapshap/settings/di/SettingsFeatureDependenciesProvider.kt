package com.arshapshap.settings.di

internal interface SettingsFeatureDependenciesProvider {

    var dependencies: SettingsFeatureDependencies

    companion object : SettingsFeatureDependenciesProvider by SettingsFeatureDependenciesStore
}

object SettingsFeatureDependenciesStore : SettingsFeatureDependenciesProvider {

    override lateinit var dependencies: SettingsFeatureDependencies
}
