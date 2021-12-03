package com.hallett.bujoass.presentation.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.hallett.bujoass.R
import com.hallett.bujoass.database.TaskGenerator
import com.hallett.bujoass.databinding.ActivityBujoAssBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class BujoAssActivity : AppCompatActivity(), KodeinAware {
    override val kodein: Kodein by closestKodein()
    private lateinit var binding: ActivityBujoAssBinding

    private val taskGenerator: TaskGenerator by instance()

    private val navHostFragment: NavHostFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
    }

    private val navController: NavController by lazy {
        navHostFragment.navController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBujoAssBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
        lifecycleScope.launch(Dispatchers.IO) {
            taskGenerator.generateTasks()
        }
    }

    private fun setupNavigation() {
        binding.bottomNavMenu.setupWithNavController(navController)

    }
}