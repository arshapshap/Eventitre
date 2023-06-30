package com.arshapshap.events.presentation

interface EventsFeatureRouter {

    fun openEvent(id: Long)

    fun openEventCreating()

    fun closeCurrentFragment()
}