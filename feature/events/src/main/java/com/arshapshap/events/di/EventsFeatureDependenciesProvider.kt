package com.arshapshap.events.di

// used code from https://gitlab.com/MihailLovch/currencyapp/

internal interface EventsFeatureDependenciesProvider {

    var dependencies: EventsFeatureDependencies

    companion object : EventsFeatureDependenciesProvider by EventsFeatureDependenciesStore
}

object EventsFeatureDependenciesStore : EventsFeatureDependenciesProvider {

    override lateinit var dependencies: EventsFeatureDependencies
}
