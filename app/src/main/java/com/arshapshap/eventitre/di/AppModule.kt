package com.arshapshap.eventitre.di

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import com.arshapshap.common.di.scopes.ApplicationScope
import com.arshapshap.files.data.observer.LifecycleObserver
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

@Module
class AppModule {

    @ApplicationScope
    @Provides
    fun provideContext(application: Application): Context {
        return application
    }

    @ApplicationScope
    @Provides
    fun providesApplicationScope() = CoroutineScope(SupervisorJob())

    @ApplicationScope
    @Provides
    fun provideContentResolver(application: Application): ContentResolver {
        return application.contentResolver
    }

    @ApplicationScope
    @Provides
    fun provideLifecycleObserverProvider(): LifecycleObserver.Provider {
        return LifecycleObserver.Provider()
    }

    @Provides
    fun provideLifecycleObserver(provider: LifecycleObserver.Provider): LifecycleObserver {
        return provider.get()
    }
}