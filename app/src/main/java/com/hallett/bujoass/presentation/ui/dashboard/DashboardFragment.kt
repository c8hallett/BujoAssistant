package com.hallett.bujoass.presentation.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
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
import kotlinx.coroutines.flow.Flow

class DashboardFragment(): BujoAssFragment() {
    private lateinit var binding: FragmentDashboardBinding

    private val viewModel: DashboardFragmentViewModel by lazy {
        ViewModelProvider(this, vmpfactory).get(DashboardFragmentViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setContent {
            TaskList(viewModel.observeDashboardItems())
        }
    }

    @Composable
    fun TaskList(dashboardItemsFlow: Flow<List<DashboardItem>>) {
        val dashboardItems = dashboardItemsFlow.collectAsState(listOf())

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
        Card(
          modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .fillMaxWidth(),
          elevation = 2.dp,
          backgroundColor = Color.White,
          shape = RoundedCornerShape(corner = CornerSize(4.dp))
        ) {
            Column(modifier =  Modifier.padding(horizontal = 8.dp, vertical = 8.dp)) {
                Text(task.task.taskName, style = MaterialTheme.typography.h6)
                Text(task.task.status.toString(), style = MaterialTheme.typography.caption)
            }
        }
    }

    @Composable
    fun HeaderItem(header: DashboardItem.HeaderItem) {
        Text(header.headerText, style = MaterialTheme.typography.h4, color = Color.White)
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