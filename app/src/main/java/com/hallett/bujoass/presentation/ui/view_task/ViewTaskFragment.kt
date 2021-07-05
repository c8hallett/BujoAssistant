package com.hallett.bujoass.presentation.ui.view_task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.ChipGroup
import com.hallett.bujoass.databinding.FragmentViewTaskBinding
import com.hallett.bujoass.domain.model.TaskStatus
import com.hallett.bujoass.presentation.gone
import com.hallett.bujoass.presentation.model.PScope
import com.hallett.bujoass.presentation.model.PresentationResult
import com.hallett.bujoass.presentation.ui.BujoAssFragment
import com.hallett.bujoass.presentation.visible
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

class ViewTaskFragment: BujoAssFragment() {
    private lateinit var binding: FragmentViewTaskBinding
    private val viewModel: ViewTaskFragmentViewModel by lazy {
        ViewModelProvider(this, vmpfactory).get(ViewTaskFragmentViewModel::class.java)
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
        val taskId = arguments?.getLong(ARGS_SELECTED)
            ?: throw IllegalStateException(
                "Launched ViewTaskDialogFragment without passing in task id. " +
                "Use ViewTaskDialogFragment.newInstance() instead."
            )
        lifecycleScope.launch {
            viewModel.observeTask(taskId).collect {
                when(it) {
                    is PresentationResult.Loading -> {}
                    is PresentationResult.Error -> {
                        when(val e = it.error) {
                            is ViewTaskFragmentViewModel.PresentationException -> when(e){
                                is ViewTaskFragmentViewModel.PresentationException.TaskNoLongerExists -> findNavController().popBackStack()
                                is ViewTaskFragmentViewModel.PresentationException.TaskIsNotScheduled -> showError("Could not reschedule--task is currently not scheduled")
                                is ViewTaskFragmentViewModel.PresentationException.UnknownFailure ->{
                                    val text = when(e.request) {
                                        ViewTaskFragmentViewModel.Request.OBSERVE -> "Failed to update task properly."
                                        ViewTaskFragmentViewModel.Request.UPDATE_STATUS -> "Could not update status of task"
                                        ViewTaskFragmentViewModel.Request.RESCHEDULE -> "Could not schedule task"
                                        ViewTaskFragmentViewModel.Request.DELETE -> "Failed to delete task"
                                    }
                                    showError(text)
                                    Timber.w(e.cause)
                                }
                            }
                        }
                    }
                    is PresentationResult.Success -> binding.run {
                        val task = it.data
                        taskName.text = task.taskName
                        scopeLabel.text = task.scopeLabel
                        when(task.status){
                            TaskStatus.INCOMPLETE -> {
                                scheduleBlock.visible()
                                statusBlock.visible()
                                val options = when(task.scope){
                                    PScope.NONE -> arrayOf(
                                        newButton("schedule"){ scheduleTask() }
                                    )
                                    PScope.DAY -> when {
                                        task.isCurrent -> arrayOf(
                                            newButton("do tomorrow"){ viewModel.deferTask() },
                                            newButton("reschedule"){ scheduleTask() }
                                        )
                                        else -> arrayOf(
                                            newButton("do today") { viewModel.moveTaskToCurrentScope(PScope.DAY) },
                                            newButton("reschedule"){ scheduleTask() }
                                        )
                                    }
                                    PScope.WEEK -> when {
                                        task.isCurrent -> arrayOf(
                                            newButton("do today") { viewModel.moveTaskToCurrentScope(PScope.DAY) },
                                            newButton("do next week") { viewModel.deferTask() },
                                            newButton("reschedule") { scheduleTask() }
                                        )
                                        else -> arrayOf(
                                            newButton("do today") { viewModel.moveTaskToCurrentScope(PScope.DAY) },
                                            newButton("do this week") { viewModel.moveTaskToCurrentScope(PScope.WEEK) },
                                            newButton("reschedule") { scheduleTask() }
                                        )
                                    }
                                    PScope.MONTH -> when {
                                        task.isCurrent -> arrayOf(
                                            newButton("do today") { viewModel.moveTaskToCurrentScope(PScope.DAY) },
                                            newButton("do next month"){ viewModel.deferTask() },
                                            newButton("reschedule"){ scheduleTask() }
                                        )
                                        else -> arrayOf(
                                            newButton("do today") { viewModel.moveTaskToCurrentScope(PScope.DAY) },
                                            newButton("do this month"){ viewModel.moveTaskToCurrentScope(PScope.MONTH) },
                                            newButton("reschedule"){ scheduleTask() }
                                        )
                                    }
                                    PScope.YEAR -> when {
                                        task.isCurrent -> arrayOf(
                                            newButton("do today") { viewModel.moveTaskToCurrentScope(PScope.DAY) },
                                            newButton("do next year") { viewModel.deferTask() },
                                            newButton("reschedule") { scheduleTask() }
                                        )
                                        else -> arrayOf(
                                            newButton("do today") { viewModel.moveTaskToCurrentScope(PScope.DAY) },
                                            newButton("do this year") { viewModel.moveTaskToCurrentScope(PScope.YEAR) },
                                            newButton("reschedule") { scheduleTask() }
                                        )
                                    }
                                }
                                val statusOptions = arrayOf(
                                    newButton("complete"){ viewModel.updateStatus(TaskStatus.COMPLETE) },
                                    newButton("cancel"){ viewModel.updateStatus(TaskStatus.CANCELLED) }
                                )
                                scheduleBlock.replaceViews(*options)
                                statusBlock.replaceViews(*statusOptions)
                            }
                            TaskStatus.CANCELLED -> {
                                scheduleBlock.gone()
                                statusBlock.visible()

                                val statusButton = newButton("uncancel") { viewModel.updateStatus(TaskStatus.INCOMPLETE) }
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

    private fun newButton(buttonLabel: String, onClick: () -> Unit): Button {
        return Button(context).apply {
            text = buttonLabel
            setOnClickListener { onClick() }
        }
    }

    private fun scheduleTask() {

    }

    private fun showError(text: String) {
        context?.let {
            Toast.makeText(it, text, Toast.LENGTH_LONG).show()
        }
    }
}