package com.arshapshap.events.presentation.screens

interface EventsFeatureRouter {

    fun openEvent(id: Long)

    fun closeCurrentFragment()
}