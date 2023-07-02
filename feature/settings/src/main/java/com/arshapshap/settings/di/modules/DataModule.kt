package com.arshapshap.settings.di.modules

import com.google.gson.Gson
import dagger.Module
import dagger.Provides

@Module
internal class DataModule {

    @Provides
    fun provideGson(): Gson
        = Gson()
}