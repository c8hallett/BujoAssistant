package com.hallett.bujoass.presentation.ui.task_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.hallett.bujoass.R
import com.hallett.bujoass.databinding.FragmentTaskListBinding
import com.hallett.bujoass.domain.model.TaskStatus
import com.hallett.bujoass.presentation.ui.BujoAssFragment
import com.hallett.bujoass.presentation.ui.view_task.ViewTaskFragment
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

class TaskListFragment: BujoAssFragment() {

    private lateinit var binding: FragmentTaskListBinding
    private val taskAdapter: TaskListAdapter = TaskListAdapter{
        findNavController().navigate(
            TaskListFragmentDirections.actionTaskListFragmentToViewTaskDialogFragment(it.id)
        )
    }

    private val viewModel: TaskListFragmentViewModel by lazy {
        ViewModelProvider(this, vmpfactory).get(TaskListFragmentViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.taskList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = taskAdapter
            val taskSwiper = TaskSwipeHelper{ position, swipe ->
                Timber.i("Swiped $swipe at position $position")
                val task = taskAdapter.getTaskAtPosition(position)
                when(swipe){
                    TaskSwipeHelper.Swipe.LEFT -> viewModel.deferTask(task)
                    TaskSwipeHelper.Swipe.RIGHT -> viewModel.updateStatus(task)
                }
            }
            ItemTouchHelper(taskSwiper).attachToRecyclerView(this)
        }
        hookupViewModelObservers()
        setOnClickListeners()
    }

    private fun hookupViewModelObservers(){
        lifecycleScope.launch {
            viewModel.observeNewScopeSelected().collect {
                binding.scopeSelector.displayScope(it)
            }
        }
        lifecycleScope.launch {
            viewModel.observeTaskList().collect {
                Timber.i("Task list updated: $it")
                taskAdapter.setItems(it)
            }
        }
        lifecycleScope.launch {
            viewModel.observeMessages().collect {
                context?.let { ctx ->
                    Toast.makeText(ctx, it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setOnClickListeners() {
        binding.run {
            scopeSelector.setOnScopeSelectedListener { viewModel.onNewScopeSelected(it) }
            newTaskBtn.setOnClickListener {
                findNavController().navigate(R.id.action_taskListFragment_to_newFragment)
            }
        }
    }
}