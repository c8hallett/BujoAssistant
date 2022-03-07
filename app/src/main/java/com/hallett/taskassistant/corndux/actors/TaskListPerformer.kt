package com.hallett.taskassistant.corndux.actors

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.hallett.corndux.Action
import com.hallett.corndux.SideEffect
import com.hallett.database.ITaskRepository
import com.hallett.domain.model.Task
import com.hallett.domain.model.TaskStatus
import com.hallett.scopes.model.ScopeType
import com.hallett.scopes.scope_generator.IScopeCalculator
import com.hallett.taskassistant.corndux.IActionPerformer
import com.hallett.taskassistant.corndux.IReducer
import com.hallett.taskassistant.corndux.TaskAssistantState
import com.hallett.taskassistant.corndux.TasksListState
import com.hallett.taskassistant.corndux.actions.CancelScopeSelection
import com.hallett.taskassistant.corndux.actions.DeferTask
import com.hallett.taskassistant.corndux.actions.DeleteTask
import com.hallett.taskassistant.corndux.actions.PerformInitialSetup
import com.hallett.taskassistant.corndux.actions.RescheduleTask
import com.hallett.taskassistant.corndux.actions.SelectNewScope
import com.hallett.taskassistant.corndux.actions.ToggleTaskComplete
import com.hallett.taskassistant.ui.navigation.TaskNavDestination
import kotlinx.coroutines.flow.Flow

class TaskListPerformer(
    private val taskRepo: ITaskRepository,
    private val scopeCalculator: IScopeCalculator
): IActionPerformer, IReducer {

    private data class UpdateTaskListTasks(
        val taskList: Flow<PagingData<Task>>
    ): Action
    private val pagingConfig = PagingConfig(pageSize = 20)

    override suspend fun performAction(
        state: TaskAssistantState,
        action: Action,
        dispatchNewAction: (Action) -> Unit
    ) {
        if (state.session.screen !is TaskNavDestination.TaskList) return
        when(action) {
            is PerformInitialSetup -> dispatchNewAction(
                SelectNewScope(scopeCalculator.generateScope(ScopeType.DAY))
            )
            is SelectNewScope -> dispatchNewAction(
                UpdateTaskListTasks(
                    taskList =  taskRepo.observeTasksForScope(
                        pagingConfig,
                        action.newTaskScope
                    )
                )
            )
            is DeleteTask -> taskRepo.deleteTask(action.task)
            is DeferTask -> {
                val nextScope = when(val oldScope = action.task.scope) {
                    null -> null
                    else -> scopeCalculator.add(oldScope, 1)
                }
                taskRepo.moveToNewScope(action.task, nextScope)
            }
            is RescheduleTask -> taskRepo.moveToNewScope(action.task, state.components.taskList.scope)
            is ToggleTaskComplete -> when(action.task.status) {
                TaskStatus.COMPLETE -> taskRepo.updateStatus(action.task, TaskStatus.INCOMPLETE)
                TaskStatus.INCOMPLETE -> {
                    taskRepo.moveToNewScope(action.task, scopeCalculator.generateScope())
                    taskRepo.updateStatus(action.task, TaskStatus.COMPLETE)
                }
            }
        }
    }

    override fun reduce(
        state: TaskAssistantState,
        action: Action,
        dispatchSideEffect: (SideEffect) -> Unit
    ): TaskAssistantState = when(action) {
        is UpdateTaskListTasks -> state.updateTaskList { copy(taskList = action.taskList) }
        else -> state
    }

    private inline fun TaskAssistantState.updateTaskList(update: TasksListState.() -> TasksListState): TaskAssistantState {
        return updateComponents { copy(taskList = taskList.update() ) }
    }
}