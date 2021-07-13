package com.hallett.bujoass.presentation.ui.task_list

import androidx.lifecycle.viewModelScope
import com.hallett.bujoass.domain.Scope
import com.hallett.bujoass.domain.model.TaskStatus
import com.hallett.bujoass.domain.usecase.modify_task.IDeferTaskUseCase
import com.hallett.bujoass.domain.usecase.modify_task.IModifyTaskStatusUseCase
import com.hallett.bujoass.domain.usecase.observe_task.IObserveTaskListFlowableUseCase
import com.hallett.bujoass.presentation.PresentationMessage
import com.hallett.bujoass.presentation.model.Task
import com.hallett.bujoass.presentation.ui.BujoAssViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber

class TaskListFragmentViewModel(
    private val observeTaskListFlowableUseCase: IObserveTaskListFlowableUseCase,
    private val modifyTaskStatusUseCase: IModifyTaskStatusUseCase,
    private val deferTaskUseCase: IDeferTaskUseCase,
): BujoAssViewModel() {

    private val messageFlow = MutableSharedFlow<PresentationMessage>()

    private var selectedScopeFlow = MutableStateFlow<Scope?>(null)
    fun onNewScopeSelected(scope: Scope?) {
        viewModelScope.launch {
            selectedScopeFlow.emit(scope)
        }
    }

    fun observeTaskList(): Flow<List<Task>> = observeTaskListFlowableUseCase.execute(selectedScopeFlow)
    fun observeNewScopeSelected(): Flow<Scope?> = selectedScopeFlow
    fun observeMessages(): Flow<PresentationMessage> = messageFlow

    fun updateStatus(task: Task?) {
        viewModelScope.launch {
            when(task) {
                null -> messageFlow.errorMessage("Invalid task.")
                else -> messageFlow.emitMessage(
                    operation = {
                        when(task.status) {
                            TaskStatus.COMPLETE -> {
                                modifyTaskStatusUseCase.execute(task.id, TaskStatus.INCOMPLETE)
                                "Task reset"
                            }
                            TaskStatus.INCOMPLETE -> {
                                modifyTaskStatusUseCase.execute(task.id, TaskStatus.COMPLETE)
                                "Task completed."
                            }
                        }
                    },
                    onFailure = {
                        Timber.w(it, "Failed to update task status (${task.status}).")
                        "Could not update task."
                    }
                )
            }
        }
    }

    fun deferTask(task: Task?) {
        viewModelScope.launch {
            when(task) {
                null -> messageFlow.errorMessage("Invalid task.")
                else -> messageFlow.emitMessage(
                    operation = {
                        deferTaskUseCase.execute(task.id)
                        "Task deferred"
                    },
                    onFailure = {
                        Timber.w(it, "Couldn't defer task.")
                        when(it){
                            is IllegalArgumentException -> "Could not defer task, it no longer exists"
                            is IllegalStateException -> "Could not defer task--it is not scheduled yet."
                            else -> "Could not defer task for some reason. Please try again."
                        }
                    }
                )
            }
        }
    }

}