package com.hallett.bujoass.presentation.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.hallett.bujoass.databinding.FragmentDashboardBinding
import com.hallett.bujoass.presentation.ui.BujoAssFragment
import timber.log.Timber
import java.lang.ref.WeakReference

class DashboardFragment(): BujoAssFragment() {
    private lateinit var binding: FragmentDashboardBinding

    private val dashboardAdapter by lazy {
        DashboardAdapter(WeakReference(requireContext())){
            Timber.i("Clicked task: $it")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDashboardBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = binding.dashboardList.run {
        adapter = dashboardAdapter
        layoutManager = LinearLayoutManager(context)
        addItemDecoration(StickyHeaderDecoration(dashboardAdapter))
    }
}