package com.arshapshap.settings.di.modules

import com.arshapshap.settings.data.repositories.EventsRepositoryImpl
import com.arshapshap.settings.domain.repositories.EventsRepository
import dagger.Binds
import dagger.Module

@Module
internal interface SettingsFeatureModule {

    @Binds
    fun bindEventsRepositoryImpl(repositoryImpl: EventsRepositoryImpl): EventsRepository
}