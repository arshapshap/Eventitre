package com.arshapshap.settings.di

import com.arshapshap.database.dao.EventDao
import com.arshapshap.files.domain.EventsJsonRepository

interface SettingsFeatureDependencies {

    fun eventsJsonRepository(): EventsJsonRepository

    fun eventDao(): EventDao
}