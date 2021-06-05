package com.hallett.bujoass

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hallett.bujoass.databinding.ActivityBujoAssBinding

class BujoAssActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBujoAssBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBujoAssBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}