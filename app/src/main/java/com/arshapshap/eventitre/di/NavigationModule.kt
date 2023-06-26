package com.arshapshap.eventitre.di

import com.arshapshap.eventitre.navigation.Navigator
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object NavigationModule {

    @Singleton
    @Provides
    fun provideNavigation(): Navigator = Navigator()
}
