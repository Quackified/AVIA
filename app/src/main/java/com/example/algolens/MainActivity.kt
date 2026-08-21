package com.example.algolens

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.algolens.databinding.ActivityMainBinding
import com.example.algolens.ui.dashboard.DashboardFragment
import com.example.algolens.ui.practice.PracticeFragment
import com.example.algolens.ui.profile.ProfileFragment
import com.example.algolens.ui.settings.SettingsFragment

/**
 * The main entry point of the AlgoLens application.
 *
 * This Activity manages the primary navigation via a BottomNavigationView.
 * Each navigation item will eventually load a different Fragment into the container.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    
    // Variant Detection Flags
    private var hasDisplayCutout: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        // 1. Enable Edge-to-Edge: This makes the status and navigation bars transparent
        // and allows the app to draw content behind them.
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. Handle Insets (Safe Areas):
        // We use window insets to detect notches (cutouts) and system bars.
        ViewCompat.setOnApplyWindowInsetsListener(binding.mainLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val displayCutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout())

            // Padding logic for the main container:
            // - LEFT/RIGHT/TOP: Padded to avoid notches and status bars.
            // - BOTTOM: 0 padding here, because we want the BottomNav background to extend
            //   to the edge of the screen (behind the navigation pill).
            v.setPadding(
                maxOf(systemBars.left, displayCutout.left),
                maxOf(systemBars.top, displayCutout.top),
                maxOf(systemBars.right, displayCutout.right),
                0 
            )

            // Variant Detection: Check for notches or special screen cutouts
            hasDisplayCutout = insets.displayCutout != null

            // Specifically apply bottom padding to the BottomNavigationView
            // so that its icons are safely above the system navigation bar.
            // We add a little bit of extra padding (12dp) for the "text margins" 
            // the user mentioned.
            val density = resources.displayMetrics.density
            val extraPadding = (12 * density).toInt()
            
            binding.bottomNavigation.setPadding(0, extraPadding / 2, 0, systemBars.bottom + extraPadding)

            insets
        }

        setupNavigation()

        // Set default fragment if nothing is selected yet
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, DashboardFragment())
                .commit()
        }
    }

    private fun setupNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.navigation_home -> DashboardFragment()
                R.id.navigation_explore -> PracticeFragment()
                R.id.navigation_profile -> ProfileFragment()
                R.id.navigation_settings -> SettingsFragment()
                else -> null
            }

            fragment?.let {
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.nav_host_fragment, it)
                    .commit()
                true
            } ?: false
        }

        // Set default selection
        binding.bottomNavigation.selectedItemId = R.id.navigation_home
    }
}