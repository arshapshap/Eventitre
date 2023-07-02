package com.arshapshap.files.di

import com.arshapshap.files.data.FilesProviderImpl
import com.arshapshap.files.domain.FilesProvider
import dagger.Binds
import dagger.Module

@Module
internal interface FilesBindsModule {

    @Binds
    fun bindFilesProviderImpl_to_FilesProvider(filesProviderImpl: FilesProviderImpl): FilesProvider
}