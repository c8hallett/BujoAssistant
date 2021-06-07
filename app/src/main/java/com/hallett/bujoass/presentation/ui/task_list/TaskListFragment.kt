package com.hallett.bujoass.presentation.ui.task_list

import android.R as androidR
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hallett.bujoass.R
import com.hallett.bujoass.databinding.FragmentTaskListBinding
import com.hallett.bujoass.presentation.ui.BujoAssFragment
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TaskListFragment: BujoAssFragment() {

    private lateinit var binding: FragmentTaskListBinding
    private val taskAdapter: TaskListAdapter = TaskListAdapter()

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
        binding.run {
            lifecycleScope.launch {
                viewModel.observeScopeOptions().collect { resList ->
                    context?.let { ctx ->
                        val spinnerOptions = resList.map { ctx.getString(it) }
                        pickScopeSpn.adapter =
                            ArrayAdapter(ctx, androidR.layout.simple_spinner_item, spinnerOptions)
                    }
                }
            }
            lifecycleScope.launch {
                viewModel.observeSelectedScopeIndex().collect { index ->
                    pickScopeSpn.setSelection(index)
                }
            }
            lifecycleScope.launch {
                viewModel.observeTaskList().collect {
                    taskAdapter.setItems(it)
                }
            }
            lifecycleScope.launch {
                viewModel.observeSelectedDate().collect {
                    dateSelector.text = it
                }
            }
        }
    }

    private fun setOnClickListeners() {
        binding.run {
            dateSelector.setOnDateSetListener{ _, year, month, dayOfMonth ->
                viewModel.selectDate(year, month, dayOfMonth)
            }
            pickScopeSpn.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) = viewModel.selectScope(position)

                override fun onNothingSelected(parent: AdapterView<*>?) = viewModel.selectScope(0)
            }
            newTaskBtn.setOnClickListener {
                findNavController().navigate(R.id.action_taskListFragment_to_newFragment)
            }
        }
    }
}