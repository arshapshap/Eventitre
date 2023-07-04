package com.arshapshap.eventitre.navigation

import androidx.navigation.NavController
import androidx.navigation.navOptions
import com.arshapshap.eventitre.R
import com.arshapshap.events.presentation.EventsFeatureRouter
import com.arshapshap.events.presentation.screens.event.EventFragment
import java.util.*

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

    override fun closeCurrentFragment() {
        navController?.popBackStack()
    }

    override fun openEvent(id: Long) {
        navController?.navigate(R.id.eventFragment, EventFragment.createBundle(id), navOptionsWithAnimations)
    }

    override fun openEventCreating(date: Date) {
        navController?.navigate(R.id.newEventFragment, EventFragment.createBundle(date), navOptionsWithAnimations)
    }

    private val navOptionsWithAnimations = navOptions {
        anim {
            enter = R.anim.slide_in_right
            exit = R.anim.slide_out_left
            popExit = R.anim.slide_out_right
            popEnter = R.anim.slide_in_left
        }
    }
}