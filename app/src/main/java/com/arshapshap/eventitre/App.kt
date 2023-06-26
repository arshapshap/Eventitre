package com.arshapshap.eventitre

import android.app.Application
import com.arshapshap.eventitre.di.AppComponent
import com.arshapshap.eventitre.di.DaggerAppComponent
import com.arshapshap.events.di.EventsFeatureDependenciesStore

class App : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent
            .builder()
            .application(this)
            .build()
        initDependencies()
    }

    private fun initDependencies() {
        EventsFeatureDependenciesStore.dependencies = appComponent
    }

}