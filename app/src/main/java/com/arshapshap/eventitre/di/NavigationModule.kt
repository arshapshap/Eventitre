package com.arshapshap.eventitre.di

import com.arshapshap.common.di.scopes.ApplicationScope
import com.arshapshap.eventitre.navigation.Navigator
import com.arshapshap.eventitre.presentation.MainRouter
import com.arshapshap.events.presentation.EventsFeatureRouter
import dagger.Module
import dagger.Provides

@Module
class NavigationModule {

    @ApplicationScope
    @Provides
    fun provideNavigation(): Navigator = Navigator()

    @ApplicationScope
    @Provides
    fun provideMainRouter(navigator: Navigator): MainRouter = navigator

    @ApplicationScope
    @Provides
    fun provideEventsFeatureRouter(navigator: Navigator): EventsFeatureRouter = navigator
}
