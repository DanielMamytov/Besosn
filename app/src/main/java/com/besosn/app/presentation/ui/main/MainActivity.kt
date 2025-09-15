package com.besosn.app.presentation.ui.main

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.besosn.app.R
import com.besosn.app.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val navGraph = navController.navInflater.inflate(R.navigation.navgraph)

        val prefs = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
        val startDest = if (prefs.getBoolean(Constants.PREF_HAS_SEEN_ONBOARDING, false)) {
            R.id.homeFragment
        } else {
            R.id.splashFragment
        }
        navGraph.setStartDestination(startDest)
        navController.graph = navGraph
    }
}
