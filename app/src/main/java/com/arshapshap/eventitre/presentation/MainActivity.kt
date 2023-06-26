package com.arshapshap.eventitre.presentation

import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.arshapshap.common.base.BaseActivity
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

    override fun initViews() {
        navController =
            (supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment).navController
        navigator.attachNavController(navController!!, R.navigation.nav_graph)
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