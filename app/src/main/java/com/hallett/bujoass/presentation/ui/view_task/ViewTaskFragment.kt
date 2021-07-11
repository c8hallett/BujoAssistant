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
import com.hallett.bujoass.presentation.model.PScope
import com.hallett.bujoass.presentation.model.PScopeInstance
import com.hallett.bujoass.presentation.model.PresentationResult
import com.hallett.bujoass.presentation.ui.ArgumentConstants
import com.hallett.bujoass.presentation.ui.BujoAssFragment
import com.hallett.bujoass.presentation.ui.SelectScopeDialogFragment
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
        super.onViewCreated(view, savedInstanceState)
        hookupViewModelObservers()
        setOnClickListeners()
    }

    private fun hookupViewModelObservers(){
        val taskId = arguments?.getLong(ArgumentConstants.ARGS_SELECTED)
            ?: throw IllegalStateException("Launched ViewTaskDialogFragment without passing in task id.")
        lifecycleScope.launch {
            viewModel.observeTask(taskId).collect {
                when(it) {
                    is PresentationResult.Loading -> {}
                    is PresentationResult.Error -> showError(it.error.message ?: "Unknown error")
                    is PresentationResult.Success -> binding.run {
                        val task = it.data
                        taskName.text = task.taskName
                        scopeLabel.text = task.scopeLabel
                        when(task.status){
                            TaskStatus.INCOMPLETE -> {
                                scheduleBlock.visible()
                                statusBlock.visible()
                                val scheduleOptions = populateButtons(task.scope, task.isCurrent)
                                val statusOptions = arrayOf(
                                    newButton("complete"){ viewModel.updateStatus(TaskStatus.COMPLETE) },
                                )
                                scheduleBlock.replaceViews(*scheduleOptions)
                                statusBlock.replaceViews(*statusOptions)
                            }
                            TaskStatus.COMPLETE -> {
                                val scheduleOptions = arrayOf<Button>()
                                val statusOptions = arrayOf<Button>()
                                scheduleBlock.replaceViews(*scheduleOptions)
                                statusBlock.replaceViews(*statusOptions)
                                // Nothing can be modified about the task--only for viewing (or deleting)
                            }
                        }
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewModel.observeDismiss().collect {
                findNavController().popBackStack()
            }
        }
        lifecycleScope.launch {
            viewModel.observeMessages().collect {
                showError(it.message)
            }
        }
    }

    private fun populateButtons(scopeInstance: PScopeInstance, isCurrent: Boolean): Array<Button> = when (scopeInstance.scope) {
        PScope.NONE -> arrayOf(
            newButton("schedule") { scheduleTask(scopeInstance) }
        )
        PScope.DAY -> when {
            isCurrent -> arrayOf(
                newButton("do tomorrow") { viewModel.deferTask() },
                newButton("reschedule") { scheduleTask(scopeInstance) }
            )
            else -> arrayOf(
                newButton("do today") { viewModel.moveTaskToCurrentScope(PScope.DAY) },
                newButton("reschedule") { scheduleTask(scopeInstance) }
            )
        }
        PScope.WEEK -> when {
            isCurrent -> arrayOf(
                newButton("do today") { viewModel.moveTaskToCurrentScope(PScope.DAY) },
                newButton("do next week") { viewModel.deferTask() },
                newButton("reschedule") { scheduleTask(scopeInstance) }
            )
            else -> arrayOf(
                newButton("do today") { viewModel.moveTaskToCurrentScope(PScope.DAY) },
                newButton("do this week") { viewModel.moveTaskToCurrentScope(PScope.WEEK) },
                newButton("reschedule") { scheduleTask(scopeInstance) }
            )
        }
        PScope.MONTH -> when {
            isCurrent -> arrayOf(
                newButton("do today") { viewModel.moveTaskToCurrentScope(PScope.DAY) },
                newButton("do next month") { viewModel.deferTask() },
                newButton("reschedule") { scheduleTask(scopeInstance) }
            )
            else -> arrayOf(
                newButton("do today") { viewModel.moveTaskToCurrentScope(PScope.DAY) },
                newButton("do this month") { viewModel.moveTaskToCurrentScope(PScope.MONTH) },
                newButton("reschedule") { scheduleTask(scopeInstance) }
            )
        }
        PScope.YEAR -> when {
            isCurrent -> arrayOf(
                newButton("do today") { viewModel.moveTaskToCurrentScope(PScope.DAY) },
                newButton("do next year") { viewModel.deferTask() },
                newButton("reschedule") { scheduleTask(scopeInstance) }
            )
            else -> arrayOf(
                newButton("do today") { viewModel.moveTaskToCurrentScope(PScope.DAY) },
                newButton("do this year") { viewModel.moveTaskToCurrentScope(PScope.YEAR) },
                newButton("reschedule") { scheduleTask(scopeInstance) }
            )
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

    private fun scheduleTask(currentScope: PScopeInstance) {
        findNavController().run {
            getNavigationResult<PScopeInstance?>(SelectScopeDialogFragment.RETURN_VALUE_SELECTED_SCOPE) {
                Timber.i("Received new scope: $it")
                viewModel.rescheduleTask(it)
            }
            navigate(
                ViewTaskFragmentDirections.actionViewTaskDialogFragmentToSelectScopeDialogFragment(currentScope)
            )
        }
    }

    private fun showError(text: String) {
        context?.let {
            Toast.makeText(it, text, Toast.LENGTH_LONG).show()
        }
    }
    }