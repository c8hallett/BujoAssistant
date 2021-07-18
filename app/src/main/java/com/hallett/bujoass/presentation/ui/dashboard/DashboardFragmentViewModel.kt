package com.hallett.bujoass.presentation.ui.dashboard

import androidx.lifecycle.viewModelScope
import com.hallett.bujoass.domain.ScopeType
import com.hallett.bujoass.domain.model.TaskStatus
import com.hallett.bujoass.domain.usecase.modify_task.IDeferTaskUseCase
import com.hallett.bujoass.domain.usecase.modify_task.IDeleteTaskUseCase
import com.hallett.bujoass.domain.usecase.modify_task.IModifyTaskStatusUseCase
import com.hallett.bujoass.domain.usecase.modify_task.IRescheduleTaskUseCase
import com.hallett.bujoass.domain.usecase.observe_task.IObserveCurrentTasksFlowableUseCase
import com.hallett.bujoass.presentation.PresentationMessage
import com.hallett.bujoass.presentation.model.Task
import com.hallett.bujoass.presentation.ui.BujoAssViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber

class DashboardFragmentViewModel(
    private val observeCurrentTasksFlowableUseCase: IObserveCurrentTasksFlowableUseCase,
    private val modifyTaskStatusUseCase: IModifyTaskStatusUseCase,
    private val deferTaskUseCase: IDeferTaskUseCase,
    private val deleteTaskUseCase: IDeleteTaskUseCase,
    private val rescheduleTaskUseCase: IRescheduleTaskUseCase,
): BujoAssViewModel() {
    private val messageFlow = MutableSharedFlow<PresentationMessage>()

    fun observeDashboardItems(): Flow<List<DashboardItem>> {
        return observeCurrentTasksFlowableUseCase.execute()
            .catch { t ->
                Timber.w(t, "Some error occurred")
            }.map { list ->
                val (day, week) = list
                    .map { DashboardItem.TaskItem(it) }
                    .partition { it.task.scope?.type == ScopeType.DAY }

                val todayHeader = listOf(DashboardItem.HeaderItem("TODAY"))
                val thisWeekHeader = listOf(DashboardItem.HeaderItem("THIS WEEK"))

                todayHeader + day + thisWeekHeader + week
            }
    }
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
            when {
                task == null -> messageFlow.errorMessage("Invalid task.")
                task.status == TaskStatus.COMPLETE -> messageFlow.errorMessage("Task is already complete. Cannot be rescheduled")
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

    fun deleteTask(task: Task?) {
        viewModelScope.launch {
            when(task) {
                null -> messageFlow.errorMessage("Invalid task")
                else -> messageFlow.emitMessage(
                    operation = {
                        deleteTaskUseCase.execute(task.id)
                        "Task deleted"
                    },
                    onFailure = {
                        Timber.w(it, "Couldn't delete task")
                        "Failed to delete task. Please try again."
                    }
                )
            }
        }
    }

    fun rescheduleTask(task: Task?){
        viewModelScope.launch {
            when {
                task == null -> messageFlow.errorMessage("Invalid task.")
                task.status == TaskStatus.COMPLETE -> messageFlow.errorMessage("Task is already complete. Cannot be rescheduled")
                else -> messageFlow.infoMessage("Coming soon")
            }
        }
    }
}