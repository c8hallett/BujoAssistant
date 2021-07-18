package com.hallett.bujoass.presentation.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.hallett.bujoass.databinding.FragmentDashboardBinding
import com.hallett.bujoass.presentation.PresentationMessage
import com.hallett.bujoass.presentation.model.Task
import com.hallett.bujoass.presentation.ui.BujoAssFragment
import com.hallett.bujoass.presentation.ui.task_list.TaskSwipeHelper
import com.hallett.bujoass.presentation.ui.task_list.TaskSwipeHelper.*
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
        val dashboardAdapter = DashboardAdapter(::clickTask, ::swipeTask)
        binding.dashboardList.run {
            adapter = dashboardAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(StickyHeaderDecoration(dashboardAdapter))
            ItemTouchHelper(TaskSwipeHelper(lifecycleScope, dashboardAdapter)).attachToRecyclerView(this)
        }

        lifecycleScope.launch {
            viewModel.observeDashboardItems().collect {
                dashboardAdapter.setItems(it)
            }
        }

        lifecycleScope.launch {
            viewModel.observeMessages().collect {
                Timber.i("Received message: $it")
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                if(it is PresentationMessage.Error){
                    Timber.i("should be resetting ui")
                    dashboardAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun clickTask(task: Task) {
        Timber.i("Task $task clicked")
    }

    private fun swipeTask(task: Task, swipe: Swipe) {
        when(swipe){
            Swipe.LEFT -> viewModel.deferTask(task)
            Swipe.HOLD_LEFT -> viewModel.rescheduleTask(task)
            Swipe.RIGHT -> viewModel.updateStatus(task)
            Swipe.HOLD_RIGHT -> viewModel.deleteTask(task)
        }
    }
}