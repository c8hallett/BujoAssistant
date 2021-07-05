package com.hallett.bujoass.presentation.ui.task_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hallett.bujoass.R
import com.hallett.bujoass.databinding.FragmentTaskListBinding
import com.hallett.bujoass.presentation.ui.BujoAssFragment
import com.hallett.bujoass.presentation.ui.view_task.ViewTaskFragment
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

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
                taskAdapter.setItems(it)
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