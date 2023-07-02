package com.arshapshap.eventitre.di

import android.app.Application
import com.arshapshap.common.di.scopes.ApplicationScope
import com.arshapshap.database.di.DatabaseModule
import com.arshapshap.eventitre.presentation.MainActivity
import com.arshapshap.events.di.EventsFeatureDependencies
import com.arshapshap.files.di.FilesModule
import com.arshapshap.settings.di.SettingsFeatureDependencies
import dagger.BindsInstance
import dagger.Component

@Component(
    modules = [AppModule::class, NavigationModule::class, DatabaseModule::class, FilesModule::class]
)
@ApplicationScope
interface AppComponent : EventsFeatureDependencies, SettingsFeatureDependencies {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(activity: MainActivity)
}