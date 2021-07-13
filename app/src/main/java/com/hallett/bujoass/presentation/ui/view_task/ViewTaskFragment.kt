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
import com.hallett.bujoass.domain.Scope
import com.hallett.bujoass.domain.model.TaskStatus
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
                                val scheduleOptions = populateButtons(task.scope)
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

    private fun populateButtons(scope: Scope?): Array<Button> = when (scope) {
        null -> arrayOf(
            newButton("schedule") { scheduleTask(scope) }
        )
        is Scope.Day -> when {
            scope.isCurrent() -> arrayOf(
                newButton("do tomorrow") { viewModel.deferTask() },
                newButton("reschedule") { scheduleTask(scope) }
            )
            else -> arrayOf(
                newButton("do today") { viewModel.moveTaskToCurrentScope(scope) },
                newButton("reschedule") { scheduleTask(scope) }
            )
        }
        is Scope.Week -> when {
            scope.isCurrent() -> arrayOf(
                newButton("do today") { viewModel.moveTaskToCurrentScope(Scope.Day()) },
                newButton("do next week") { viewModel.deferTask() },
                newButton("reschedule") { scheduleTask(scope) }
            )
            else -> arrayOf(
                newButton("do today") { viewModel.moveTaskToCurrentScope(Scope.Day()) },
                newButton("do this week") { viewModel.moveTaskToCurrentScope(scope) },
                newButton("reschedule") { scheduleTask(scope) }
            )
        }
        is Scope.Month -> when {
            scope.isCurrent() -> arrayOf(
                newButton("do today") { viewModel.moveTaskToCurrentScope(Scope.Day()) },
                newButton("do next month") { viewModel.deferTask() },
                newButton("reschedule") { scheduleTask(scope) }
            )
            else -> arrayOf(
                newButton("do today") { viewModel.moveTaskToCurrentScope(Scope.Day()) },
                newButton("do this month") { viewModel.moveTaskToCurrentScope(scope) },
                newButton("reschedule") { scheduleTask(scope) }
            )
        }
        is Scope.Year -> when {
            scope.isCurrent() -> arrayOf(
                newButton("do today") { viewModel.moveTaskToCurrentScope(Scope.Day()) },
                newButton("do next year") { viewModel.deferTask() },
                newButton("reschedule") { scheduleTask(scope) }
            )
            else -> arrayOf(
                newButton("do today") { viewModel.moveTaskToCurrentScope(Scope.Day()) },
                newButton("do this year") { viewModel.moveTaskToCurrentScope(scope) },
                newButton("reschedule") { scheduleTask(scope) }
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

    private fun scheduleTask(scope: Scope?) {
        findNavController().run {
            getNavigationResult<Scope?>(SelectScopeDialogFragment.RETURN_VALUE_SELECTED_SCOPE) {
                Timber.i("Received new scope: $it")
                viewModel.rescheduleTask(it)
            }
            navigate(
                ViewTaskFragmentDirections.actionViewTaskDialogFragmentToSelectScopeDialogFragment(scope)
            )
        }
    }

    private fun showError(text: String) {
        context?.let {
            Toast.makeText(it, text, Toast.LENGTH_LONG).show()
        }
    }
    }