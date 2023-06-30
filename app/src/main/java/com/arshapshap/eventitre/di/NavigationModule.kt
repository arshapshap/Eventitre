package com.arshapshap.eventitre.di

import com.arshapshap.common.di.scopes.ApplicationScope
import com.arshapshap.eventitre.navigation.Navigator
import com.arshapshap.events.presentation.screens.EventsFeatureRouter
import dagger.Module
import dagger.Provides

@Module
object NavigationModule {

    @ApplicationScope
    @Provides
    fun provideNavigation(): Navigator = Navigator()

    @ApplicationScope
    @Provides
    fun provideEventsFeatureRouter(navigator: Navigator): EventsFeatureRouter = navigator
}
