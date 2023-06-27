package com.arshapshap.events.di.modules

import com.arshapshap.events.data.EventsRepositoryImpl
import com.arshapshap.events.domain.interfaces.EventsRepository
import dagger.Binds
import dagger.Module

@Module
internal interface EventsFeatureModule {

    @Binds
    fun bindEventsRepositoryImpl_to_EventsRepository(repositoryImpl: EventsRepositoryImpl): EventsRepository
}