package com.arshapshap.events.presentation

import java.util.Date

interface EventsFeatureRouter {

    fun openEvent(id: Long)

    fun openEventCreating(date: Date)

    fun closeCurrentFragment()
}