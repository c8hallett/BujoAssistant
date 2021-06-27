package com.hallett.bujoass.presentation.ui.view_task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.hallett.bujoass.databinding.FragmentViewTaskBinding
import com.hallett.bujoass.domain.model.TaskStatus
import com.hallett.bujoass.presentation.gone
import com.hallett.bujoass.presentation.model.PScope
import com.hallett.bujoass.presentation.setVis
import com.hallett.bujoass.presentation.ui.BujoAssDialogFragment
import com.hallett.bujoass.presentation.visible
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ViewTaskDialogFragment: BujoAssDialogFragment() {
    override val isFullScreen: Boolean = false
    private lateinit var binding: FragmentViewTaskBinding
    private val viewModel: ViewTaskDialogFragmentViewModel by lazy {
        ViewModelProvider(this, vmpfactory).get(ViewTaskDialogFragmentViewModel::class.java)
    }

    companion object{
        private const val EXTRA_TASK_ID = "extra_task_id"

        fun newInstance(taskId: Long): ViewTaskDialogFragment{
            return ViewTaskDialogFragment().apply {
                arguments = Bundle().apply {
                    putLong(EXTRA_TASK_ID, taskId)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        hookupViewModelObservers()
        setOnClickListeners()
    }

    private fun hookupViewModelObservers(){
        binding.apply {
            lifecycleScope.launch {
                val taskId = arguments?.getLong(EXTRA_TASK_ID) ?: throw IllegalStateException("Launched ViewTaskDialogFragment without passing in task id. Use ViewTaskDialogFragment.newInstance() instead.")
                viewModel.observeTask(taskId).collect {
                    taskName.text = it.taskName
                    scopeLabel.text = it.scopeLabel
                    when(it.status){
                        TaskStatus.INCOMPLETE -> {
                            rescheduleBtn.visible()
                            deferBtn.visible()
                            completeBtn.visible()
                            cancelBtn.visible()
                            rescheduleBtn.text = "reschedule"
                            when(it.scope){
                                PScope.NONE -> {
                                    deferBtn.gone()
                                    rescheduleBtn.text = "schedule"
                                }
                                PScope.DAY -> {
                                    // TODO: only say tomorrow when date of task is "today"
                                    deferBtn.text = "tomorrow"
                                }
                                // TODO: for scopes bigger than "DAY"
                                PScope.WEEK -> {
                                    deferBtn.text = "next week"
                                }
                                PScope.MONTH -> {
                                    deferBtn.text = "next month"
                                }
                                PScope.YEAR -> {
                                    deferBtn.text = "next year"
                                }
                            }
                        }
                        TaskStatus.CANCELLED -> {
                            rescheduleBtn.gone()
                            deferBtn.gone()
                            completeBtn.gone()
                            cancelBtn.visible()
                            cancelBtn.text = "uncancel task"
                        }
                        TaskStatus.COMPLETE -> {
                            rescheduleBtn.gone()
                            deferBtn.gone()
                            completeBtn.gone()
                            cancelBtn.gone()
                        }
                    }
                }
            }
        }
    }

    private fun setOnClickListeners() {
        binding.run {
            deferBtn.setOnClickListener {  }
            rescheduleBtn.setOnClickListener {  }
            deleteBtn.setOnClickListener {  }
            cancelBtn.setOnClickListener {  }
            completeBtn.setOnClickListener {  }
        }
    }
}