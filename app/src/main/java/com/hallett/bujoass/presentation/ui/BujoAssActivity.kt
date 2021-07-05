package com.hallett.bujoass.presentation.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.hallett.bujoass.R
import com.hallett.bujoass.databinding.ActivityBujoAssBinding

class BujoAssActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBujoAssBinding


    private val navController: NavController by lazy {
        (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBujoAssBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}