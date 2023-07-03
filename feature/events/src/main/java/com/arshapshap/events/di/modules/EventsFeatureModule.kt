package com.arshapshap.events.di.modules

import com.arshapshap.events.data.repositories.EventsRepositoryImpl
import com.arshapshap.events.domain.repositories.EventsRepository
import dagger.Binds
import dagger.Module

@Module
internal interface EventsFeatureModule {

    @Binds
    fun bindEventsRepositoryImpl(repositoryImpl: EventsRepositoryImpl): EventsRepository
}