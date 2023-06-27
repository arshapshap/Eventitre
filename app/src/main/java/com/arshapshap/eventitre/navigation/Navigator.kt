package com.arshapshap.eventitre.navigation

import androidx.core.os.bundleOf
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

    override fun openEvent(id: Int) {
        navController?.navigate(
            resId = R.id.action_calendarFragment_to_eventFragment,
            args = bundleOf(EventFragment.EVENT_ID_KEY to id)
        )
    }

    override fun closeCurrentFragment() {
        navController?.popBackStack()
    }
}