package com.arshapshap.eventitre.presentation

import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.arshapshap.common_ui.base.BaseActivity
import com.arshapshap.eventitre.App
import com.arshapshap.eventitre.R
import com.arshapshap.eventitre.databinding.ActivityMainBinding
import com.arshapshap.eventitre.navigation.Navigator
import javax.inject.Inject

class MainActivity : BaseActivity<ActivityMainBinding>(
    ActivityMainBinding::inflate
) {
    @Inject
    lateinit var navigator: Navigator
    private var navController: NavController? = null

    @Inject
    lateinit var router: MainRouter

    override fun initViews() {
        navController =
            (supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment).navController

        navController?.let {
            navigator.attachNavController(it, R.navigation.nav_graph)
            configureToolbar(it)
        }
    }

    private fun configureToolbar(navController: NavController) {
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        with (binding.toolbar) {
            setupWithNavController(navController, appBarConfiguration)

            menu.getItem(0).setOnMenuItemClickListener { _ ->
                router.openSettings()
                true
            }
        }
    }

    override fun inject() {
        (application as App).appComponent
            .inject(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        navController?.let {
            navigator.detachNavController(it)
        }
        navController = null
    }
}