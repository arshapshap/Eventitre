package com.arshapshap.database.di

import android.content.Context
import com.arshapshap.common.di.scopes.ApplicationScope
import com.arshapshap.database.AppDatabase
import com.arshapshap.database.dao.EventDao
import dagger.Module
import dagger.Provides

@Module
class DatabaseModule {

    @Provides
    @ApplicationScope
    fun provideAppDatabase(context: Context): AppDatabase {
        return AppDatabase.get(context)
    }

    @Provides
    @ApplicationScope
    fun provideEventDao(appDatabase: AppDatabase): EventDao {
        return appDatabase.eventDao()
    }
}