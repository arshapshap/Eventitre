package com.arshapshap.events.di

import com.arshapshap.database.dao.EventDao
import com.arshapshap.events.presentation.screens.EventsFeatureRouter

interface EventsFeatureDependencies {

    fun eventsFeatureRouter(): EventsFeatureRouter

    fun eventDao(): EventDao
}