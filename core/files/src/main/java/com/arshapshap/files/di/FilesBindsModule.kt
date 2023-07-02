package com.arshapshap.files.di

import com.arshapshap.files.data.providers.FilesReaderImpl
import com.arshapshap.files.data.providers.FilesWriterImpl
import com.arshapshap.files.data.repositories.EventsJsonRepositoryImpl
import com.arshapshap.files.domain.repositories.EventsJsonRepository
import com.arshapshap.files.domain.FilesReader
import com.arshapshap.files.domain.FilesWriter
import dagger.Binds
import dagger.Module

@Module
internal interface FilesBindsModule {

    @Binds
    fun bindFilesReaderImpl(filesReaderImpl: FilesReaderImpl): FilesReader

    @Binds
    fun bindFilesWriterImpl(filesWriterImpl: FilesWriterImpl): FilesWriter

    @Binds
    fun bindEventsJsonRepositoryImpl(repositoryImpl: EventsJsonRepositoryImpl): EventsJsonRepository
}