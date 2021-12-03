package com.hallett.bujoass.presentation.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
            ItemTouchHelper(TaskSwipeHelper(dashboardAdapter)).attachToRecyclerView(this)
        }

        lifecycleScope.launch {
            viewModel.observeDashboardItems().collect {
                val dashboardItems = v
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

    @Composable
    fun TaskList() {
        val dashboardItems = viewModel.observeDashboardItems().collectAsState(listOf())

        LazyColumn {
            items(dashboardItems.value) { dashboardItem ->
                when(dashboardItem) {
                    is DashboardItem.TaskItem -> TaskItem(task = dashboardItem)
                    is DashboardItem.HeaderItem -> HeaderItem(header = dashboardItem)
                }
            }
        }
    }

    @Composable
    fun TaskItem(task: DashboardItem.TaskItem) {
        Row{
            Text()
        }
        Row{

        }

    }

    @Composable
    fun HeaderItem(header: DashboardItem.HeaderItem) {

    }


    private fun clickTask(task: Task) {
        Timber.i("Task $task clicked")
    }

    private fun swipeTask(task: Task, swipe: Swipe) {
        when(swipe){
            Swipe.LEFT -> viewModel.deferTask(task)
            Swipe.RIGHT -> viewModel.updateStatus(task)
        }
    }
}