package com.arshapshap.eventitre.di

import android.app.Application
import com.arshapshap.common.di.scopes.ApplicationScope
import com.arshapshap.eventitre.presentation.MainActivity
import com.arshapshap.events.di.EventsFeatureDependencies
import dagger.BindsInstance
import dagger.Component

@Component(
    modules = [AppModule::class, NavigationModule::class]
)
@ApplicationScope
interface AppComponent : EventsFeatureDependencies {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(activity: MainActivity)
}