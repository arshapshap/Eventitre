package com.arshapshap.settings.di

import com.arshapshap.database.dao.EventDao
import com.arshapshap.files.domain.FilesProvider

interface SettingsFeatureDependencies {

    fun filesProvider(): FilesProvider

    fun eventDao(): EventDao
}