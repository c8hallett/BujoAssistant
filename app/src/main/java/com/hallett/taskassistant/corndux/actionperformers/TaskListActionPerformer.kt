package com.hallett.taskassistant.corndux.actionperformers

import androidx.paging.PagingConfig
import com.hallett.database.ITaskRepository
import com.hallett.scopes.scope_generator.IScopeCalculator
import com.hallett.taskassistant.corndux.DeferTask
import com.hallett.taskassistant.corndux.DeleteTask
import com.hallett.taskassistant.corndux.IActionPerformer
import com.hallett.taskassistant.corndux.PerformInitialSetup
import com.hallett.taskassistant.corndux.RescheduleTask
import com.hallett.taskassistant.corndux.SelectNewScope
import com.hallett.taskassistant.corndux.TaskAssistantAction
import com.hallett.taskassistant.corndux.TaskAssistantSideEffect
import com.hallett.taskassistant.corndux.TaskAssistantState
import com.hallett.taskassistant.corndux.TasksListState
import com.hallett.taskassistant.ui.navigation.TaskNavDestination

class TaskListActionPerformer(
    private val taskRepository: ITaskRepository,
    private val scopeCalculator: IScopeCalculator
    ): IActionPerformer {

    private val pagingConfig = PagingConfig(pageSize = 20)

    override suspend fun performAction(
        action: TaskAssistantAction,
        state: TaskAssistantState,
        dispatchNewAction: (TaskAssistantAction) -> Unit,
        dispatchSideEffect: (TaskAssistantSideEffect) -> Unit
    ): TaskAssistantState {
        if (state.session.screen !is TaskNavDestination.TaskList) return state
        return when (action) {
            is PerformInitialSetup -> {
                val todayScope = scopeCalculator.generateScope()
                val taskList = taskRepository.observeTasksForScope(pagingConfig, todayScope)
                state.updateTaskList { copy(taskList = taskList, scope = todayScope) }
            }
            is SelectNewScope -> {
                val taskList = taskRepository.observeTasksForScope(pagingConfig, state.components.taskList.scope)
                state.updateTaskList { copy(taskList = taskList) }
            }
            is DeleteTask -> {
                taskRepository.deleteTask(action.task)
                state
            }
            is DeferTask -> {
                val nextScope = when(val oldScope = action.task.scope) {
                    null -> null
                    else -> scopeCalculator.add(oldScope, 1)
                }
                taskRepository.moveToNewScope(action.task, nextScope)
                state
            }
            is RescheduleTask -> {
                taskRepository.moveToNewScope(action.task, state.components.taskList.scope)
                state
            }

            else -> state
        }
    }

    private inline fun TaskAssistantState.updateTaskList(update: TasksListState.() -> TasksListState): TaskAssistantState {
        return updateComponents { copy(taskList = taskList.update() ) }
    }
}
