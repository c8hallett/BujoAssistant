package com.hallett.bujoass.presentation.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.hallett.bujoass.databinding.FragmentDashboardBinding
import com.hallett.bujoass.presentation.ui.BujoAssFragment
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.ref.WeakReference

class DashboardFragment(): BujoAssFragment() {
    private lateinit var binding: FragmentDashboardBinding

    private val viewModel: DashboardFragmentViewModel by lazy {
        ViewModelProvider(this, vmpfactory).get(DashboardFragmentViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDashboardBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val dashboardAdapter = DashboardAdapter(WeakReference(view.context)){
            Timber.i("Clicked task: $it")
        }
        binding.dashboardList.run {
            adapter = dashboardAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(StickyHeaderDecoration(dashboardAdapter))
        }

        lifecycleScope.launch {
            viewModel.observeDashboardItems().collect {
                dashboardAdapter.setItems(it)
            }
        }
    }
}