package com.arshapshap.files.di

import com.arshapshap.files.data.providers.FilesProviderImpl
import com.arshapshap.files.data.repositories.EventsJsonRepositoryImpl
import com.arshapshap.files.domain.EventsJsonRepository
import com.arshapshap.files.domain.FilesProvider
import dagger.Binds
import dagger.Module

@Module
internal interface FilesBindsModule {

    @Binds
    fun bindFilesProviderImpl(filesProviderImpl: FilesProviderImpl): FilesProvider

    @Binds
    fun bindEventsJsonRepositoryImpl(repositoryImpl: EventsJsonRepositoryImpl): EventsJsonRepository
}