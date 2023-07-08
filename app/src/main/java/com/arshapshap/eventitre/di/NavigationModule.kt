package com.arshapshap.eventitre.di

import com.arshapshap.common.di.scopes.ApplicationScope
import com.arshapshap.eventitre.navigation.Navigator
import com.arshapshap.events.presentation.EventsFeatureRouter
import com.arshapshap.settings.presentation.SettingsFeatureRouter
import dagger.Module
import dagger.Provides

@Module
class NavigationModule {

    @ApplicationScope
    @Provides
    fun provideNavigation(): Navigator = Navigator()

    @ApplicationScope
    @Provides
    fun provideEventsFeatureRouter(navigator: Navigator): EventsFeatureRouter = navigator

    @ApplicationScope
    @Provides
    fun provideSettingsFeatureRouter(navigator: Navigator): SettingsFeatureRouter = navigator
}
