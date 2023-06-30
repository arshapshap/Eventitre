package com.arshapshap.eventitre.di

import android.app.Application
import android.content.Context
import com.arshapshap.common.di.scopes.ApplicationScope
import dagger.Module
import dagger.Provides

@Module
object AppModule {

    @ApplicationScope
    @Provides
    fun provideContext(application: Application): Context {
        return application
    }
}