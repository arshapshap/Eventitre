package com.arshapshap.settings.di

import com.arshapshap.database.dao.EventDao
import com.arshapshap.files.domain.repositories.EventsJsonRepository
import com.arshapshap.settings.presentation.SettingsFeatureRouter

interface SettingsFeatureDependencies {

    fun eventsJsonRepository(): EventsJsonRepository

    fun eventDao(): EventDao

    fun settingsFeatureRouter(): SettingsFeatureRouter
}