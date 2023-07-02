package com.arshapshap.settings.di.modules

import com.arshapshap.settings.data.repositories.EventsJsonRepositoryImpl
import com.arshapshap.settings.domain.repositories.EventsJsonRepository
import dagger.Binds
import dagger.Module

@Module(includes = [DataModule::class])
internal interface SettingsFeatureModule {

    @Binds
    fun bindEventsRepositoryImpl_to_EventsRepository(repositoryImpl: EventsJsonRepositoryImpl): EventsJsonRepository
}