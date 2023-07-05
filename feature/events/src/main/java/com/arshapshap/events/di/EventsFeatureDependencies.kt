package com.arshapshap.events.di

import com.arshapshap.database.dao.EventDao
import com.arshapshap.events.presentation.EventsFeatureRouter
import com.arshapshap.files.domain.repositories.EventsJsonRepository

interface EventsFeatureDependencies {

    fun eventsFeatureRouter(): EventsFeatureRouter

    fun eventsJsonRepository(): EventsJsonRepository

    fun eventDao(): EventDao
}