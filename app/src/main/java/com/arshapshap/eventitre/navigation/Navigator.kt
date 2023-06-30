package com.arshapshap.eventitre.navigation

import androidx.navigation.NavController
import com.arshapshap.eventitre.R
import com.arshapshap.events.presentation.screens.EventsFeatureRouter
import com.arshapshap.events.presentation.screens.event.EventFragment

class Navigator: EventsFeatureRouter {

    private var navController: NavController? = null

    fun attachNavController(navController: NavController, graph: Int) {
        navController.setGraph(graph)
        this.navController = navController
    }

    fun detachNavController(navController: NavController) {
        if (this.navController == navController) {
            this.navController = null
        }
    }

    override fun openEvent(id: Long) {
        navController?.navigate(R.id.eventFragment, EventFragment.createBundle(id))
    }

    override fun openEventCreating() {
        navController?.navigate(R.id.eventFragment)
    }

    override fun closeCurrentFragment() {
        navController?.popBackStack()
    }
}