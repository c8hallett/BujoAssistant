package com.hallett.bujoass.presentation.ui.view_task

import androidx.lifecycle.viewModelScope
import com.hallett.bujoass.domain.Scope
import com.hallett.bujoass.domain.ScopeType
import com.hallett.bujoass.domain.model.TaskStatus
import com.hallett.bujoass.domain.usecase.modify_task.IDeferTaskUseCase
import com.hallett.bujoass.domain.usecase.modify_task.IDeleteTaskUseCase
import com.hallett.bujoass.domain.usecase.modify_task.IModifyTaskStatusUseCase
import com.hallett.bujoass.domain.usecase.modify_task.IRescheduleTaskUseCase
import com.hallett.bujoass.domain.usecase.observe_task.IObserveSingleTaskUseCase
import com.hallett.bujoass.presentation.PresentationMessage
import com.hallett.bujoass.presentation.ui.BujoAssViewModel
import com.hallett.bujoass.presentation.format.Formatter
import com.hallett.bujoass.presentation.model.PresentationResult
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class ViewTaskFragmentViewModel(
    private val observeSingleTaskUseCase: IObserveSingleTaskUseCase,
    private val modifyTaskStatusUseCase: IModifyTaskStatusUseCase,
    private val rescheduleTaskUseCase: IRescheduleTaskUseCase,
    private val deferTaskUseCase: IDeferTaskUseCase,
    private val deleteTaskUseCase: IDeleteTaskUseCase,
    private val scopeFormatter: Formatter<Scope?>,
): BujoAssViewModel() {

    private val taskFlow = MutableSharedFlow<PresentationResult<ViewableTask>>()
    private val messageFlow = MutableSharedFlow<PresentationMessage>()
    private val dismissFlow = MutableSharedFlow<Unit>()

    private var taskId: Long = -1L
    fun observeTask(taskId: Long): Flow<PresentationResult<ViewableTask>> {
        this.taskId = taskId
        viewModelScope.launch {
            observeSingleTaskUseCase.execute(taskId)
                .catch { t->
                    Timber.w(t, "Failed to receive task update.")
                    dismissFlow.emit(Unit)
                }
                .collect {
                    val task = ViewableTask(
                        it.taskName,
                        it.scope,
                        scopeFormatter.format(it.scope),
                        it.status,
                    )
                    this@ViewTaskFragmentViewModel.taskFlow.successMessage(task)
                }
        }
        return taskFlow
    }
    fun observeMessages(): Flow<PresentationMessage> = messageFlow

    fun observeDismiss(): Flow<Unit> = dismissFlow
    fun updateStatus(status: TaskStatus) {
        viewModelScope.launch {
            messageFlow.emitMessage(
                operation = {
                    modifyTaskStatusUseCase.execute(taskId, status)
                    when(status) {
                        TaskStatus.COMPLETE -> "Marked task as complete!"
                        TaskStatus.INCOMPLETE -> "Reset task."
                    }
                },
                onFailure = {
                    Timber.w(it, "Failed to update task status ($status).")
                    when(status) {
                        TaskStatus.COMPLETE -> "Couldn't mark task as complete. Please try again."
                        TaskStatus.INCOMPLETE -> "Couldn't reset task status. Please try again."
                    }
                }
            )
        }
    }

    fun deferTask() {
        viewModelScope.launch {
            messageFlow.emitMessage(
                operation = {
                    deferTaskUseCase.execute(taskId)
                    "Task deferred"
                },
                onFailure = {
                    Timber.w(it, "Couldn't defer task.")
                    when(it){
                        is IllegalArgumentException -> {
                            dismissFlow.emit(Unit)
                            null
                        }
                        is IllegalStateException -> "Could not defer task--it is not scheduled yet."
                        else -> "Could not defer task for some reason. Please try again."
                    }
                }
            )
        }
    }

    fun rescheduleTask(scope: Scope?) {
        viewModelScope.launch {
            messageFlow.emitMessage(
                operation = {
                    rescheduleTaskUseCase.execute(taskId, scope)
                    "Task rescheduled"
                },
                onFailure = {
                    Timber.w(it, "Couldn't reschedule task.")
                    "Could not reschedule task for some reason. Please try again."
                }
            )
        }
    }

    fun deleteTask() {
        viewModelScope.launch {
            try {
                deleteTaskUseCase.execute(taskId)
                dismissFlow.emit(Unit)
            } catch(t: Throwable) {
                messageFlow.errorMessage("Failed to delete task. Please try again.")
            }
        }
    }

    fun moveTaskToCurrentScope(scopeType: ScopeType) {
        viewModelScope.launch {
            messageFlow.emitMessage(
                operation = {
                    // TODO: maybe this could be moved to own use case. . .
                    rescheduleTaskUseCase.execute(taskId, Scope.getCurrent(scopeType))
                    when(scopeType) {
                        ScopeType.DAY-> "Task moved to today"
                        ScopeType.WEEK -> "Task moved to this week"
                        ScopeType.MONTH -> "Task moved to this month"
                        ScopeType.YEAR -> "Task moved to this year"
                    }
                },
                onFailure = {
                    Timber.w(it, "Couldn't move task to current scope ($scopeType)")
                    when(scopeType) {
                        ScopeType.DAY -> "Couldn't move task to today."
                        ScopeType.WEEK -> "Couldn't move task to this week."
                        ScopeType.MONTH -> "Couldn't move task to this month."
                        ScopeType.YEAR -> "Couldn't move task to this year."
                    }.plus(" Please try again.")
                }
            )
        }
    }
}