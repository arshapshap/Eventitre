package com.arshapshap.events.di

import com.arshapshap.common.di.base.BaseFeatureViewModel

internal class EventsFeatureViewModel : BaseFeatureViewModel<EventsFeatureComponent>() {
    override val component = DaggerEventsFeatureComponent.builder()
        .withDependencies(EventsFeatureDependenciesStore.dependencies)
        .build()
}