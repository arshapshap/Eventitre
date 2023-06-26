package com.arshapshap.eventitre.di

import com.arshapshap.common.di.scopes.ApplicationScope
import com.arshapshap.eventitre.navigation.Navigator
import dagger.Module
import dagger.Provides

@Module
object NavigationModule {

    @ApplicationScope
    @Provides
    fun provideNavigation(): Navigator = Navigator()
}
