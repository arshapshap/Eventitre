package com.arshapshap.eventitre

import android.app.Application
import com.arshapshap.eventitre.di.AppComponent
import com.arshapshap.eventitre.di.DaggerAppComponent

class App : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent
            .builder()
            .application(this)
            .build()
        initDeps()
    }

    private fun initDeps() {
    }

}