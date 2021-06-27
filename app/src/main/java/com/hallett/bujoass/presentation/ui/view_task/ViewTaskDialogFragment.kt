package com.hallett.bujoass.presentation.ui.view_task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.chip.ChipGroup
import com.hallett.bujoass.databinding.FragmentViewTaskBinding
import com.hallett.bujoass.domain.model.TaskStatus
import com.hallett.bujoass.presentation.gone
import com.hallett.bujoass.presentation.model.PScope
import com.hallett.bujoass.presentation.model.PScopeInstance
import com.hallett.bujoass.presentation.ui.BujoAssDialogFragment
import com.hallett.bujoass.presentation.visible
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

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
        val taskId = arguments?.getLong(EXTRA_TASK_ID)
            ?: throw IllegalStateException(
                "Launched ViewTaskDialogFragment without passing in task id. " +
                "Use ViewTaskDialogFragment.newInstance() instead."
            )
        lifecycleScope.launch {
            viewModel.observeTask(taskId).collect {
                binding.run {
                    taskName.text = it.taskName
                    scopeLabel.text = it.scopeLabel
                    when(it.status){
                        TaskStatus.INCOMPLETE -> {
                            scheduleBlock.visible()
                            statusBlock.visible()
                            val options = when(it.scope){
                                PScope.NONE -> arrayOf(
                                    Button(context).apply {
                                        text = "schedule"
                                        setOnClickListener {
                                            // TODO: schedule task
                                        }
                                    }
                                )
                                PScope.DAY -> when {
                                    it.isCurrent -> arrayOf(
                                        Button(context).apply {
                                            text = "do tomorrow"
                                            setOnClickListener {
                                                viewModel.deferTask()
                                            }
                                        },
                                        Button(context).apply {
                                            text = "reschedule"
                                            setOnClickListener {
                                                // TODO: schedule task
                                            }
                                        }
                                    )
                                    else -> arrayOf(
                                        Button(context).apply {
                                            text = "do today"
                                            setOnClickListener {
                                                viewModel.rescheduleTask(PScopeInstance(PScope.DAY, Date()))
                                            }
                                        },
                                        Button(context).apply {
                                            text = "reschedule"
                                            setOnClickListener {
                                                // TODO: schedule task
                                            }
                                        }
                                    )
                                }
                                PScope.WEEK -> arrayOf(
                                    Button(context).apply {
                                        text = "do today"
                                        setOnClickListener {
                                            viewModel.rescheduleTask(PScopeInstance(PScope.DAY, Date()))
                                        }
                                    },
                                    Button(context).apply {
                                        text = "do next week"
                                        setOnClickListener {
                                            viewModel.deferTask()
                                        }
                                    },
                                    Button(context).apply {
                                        text = "reschedule"
                                        setOnClickListener {
                                            // TODO: schedule task
                                        }
                                    }
                                )
                                PScope.MONTH -> arrayOf(
                                    Button(context).apply {
                                        text = "do today"
                                        setOnClickListener {
                                            viewModel.rescheduleTask(PScopeInstance(PScope.DAY, Date()))
                                        }
                                    },
                                    Button(context).apply {
                                        text = "do next month"
                                        setOnClickListener {
                                            viewModel.deferTask()
                                        }
                                    },
                                    Button(context).apply {
                                        text = "reschedule"
                                        setOnClickListener {
                                            // TODO: schedule task
                                        }
                                    }
                                )
                                PScope.YEAR -> arrayOf(
                                    Button(context).apply {
                                        text = "do today"
                                        setOnClickListener {
                                            viewModel.rescheduleTask(PScopeInstance(PScope.DAY, Date()))
                                        }
                                    },
                                    Button(context).apply {
                                        text = "do next year"
                                        setOnClickListener {
                                            viewModel.deferTask()
                                        }
                                    },
                                    Button(context).apply {
                                        text = "reschedule"
                                        setOnClickListener {
                                            // TODO: schedule task
                                        }
                                    }
                                )
                            }
                            val statusOptions = arrayOf(
                                Button(context).apply {
                                    text = "complete"
                                    setOnClickListener {
                                        viewModel.updateStatus(TaskStatus.COMPLETE)
                                    }
                                },
                                Button(context).apply {
                                    text = "cancel"
                                    setOnClickListener {
                                        viewModel.updateStatus(TaskStatus.CANCELLED)
                                    }
                                }
                            )
                            scheduleBlock.replaceViews(*options)
                            statusBlock.replaceViews(*statusOptions)
                        }
                        TaskStatus.CANCELLED -> {
                            scheduleBlock.gone()
                            statusBlock.visible()
                            val statusButton = Button(context).apply {
                                text = "uncancel"
                                setOnClickListener {
                                    viewModel.updateStatus(TaskStatus.INCOMPLETE)
                                }
                            }
                            statusBlock.replaceViews(statusButton)
                        }
                        TaskStatus.COMPLETE -> {
                            scheduleBlock.gone()
                            statusBlock.gone()
                            // Nothing can be modified about the task--only for viewing (or deleting)
                        }
                    }
                }
            }
        }
    }

    private fun ChipGroup.replaceViews(vararg views: View) {
        removeAllViews()
        views.forEach {
            addView(it)
        }
    }

    private fun setOnClickListeners() {
        binding.deleteBtn.setOnClickListener { viewModel.deleteTask() }
    }
}