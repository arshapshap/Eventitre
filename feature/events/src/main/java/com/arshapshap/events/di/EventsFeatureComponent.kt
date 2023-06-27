package com.arshapshap.events.di

import com.arshapshap.common.di.base.BaseFeatureComponent
import com.arshapshap.common.di.scopes.FeatureScope
import com.arshapshap.events.di.modules.EventsFeatureModule
import com.arshapshap.events.presentation.screens.calendar.CalendarFragment
import com.arshapshap.events.presentation.screens.calendar.CalendarViewModel
import dagger.Component

@Component(
    modules = [EventsFeatureModule::class],
    dependencies = [EventsFeatureDependencies::class]
)
@FeatureScope
internal interface EventsFeatureComponent : BaseFeatureComponent {

    @Component.Builder
    interface Builder {

        fun withDependencies(dependencies: EventsFeatureDependencies): Builder

        fun build(): EventsFeatureComponent
    }

    fun inject(fragment: CalendarFragment)
    fun calendarViewModel(): CalendarViewModel.Factory
}