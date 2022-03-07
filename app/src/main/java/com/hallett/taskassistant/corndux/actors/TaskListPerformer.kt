package com.hallett.taskassistant.corndux.actors

import com.hallett.corndux.Action
import com.hallett.database.ITaskRepository
import com.hallett.domain.model.TaskStatus
import com.hallett.scopes.scope_generator.IScopeCalculator
import com.hallett.taskassistant.corndux.IActionPerformer
import com.hallett.taskassistant.corndux.TaskAssistantState
import com.hallett.taskassistant.corndux.actions.DeferTask
import com.hallett.taskassistant.corndux.actions.DeleteTask
import com.hallett.taskassistant.corndux.actions.PerformInitialSetup
import com.hallett.taskassistant.corndux.actions.RescheduleTask
import com.hallett.taskassistant.corndux.actions.SelectNewScope
import com.hallett.taskassistant.corndux.actions.ToggleTaskComplete
import com.hallett.taskassistant.ui.navigation.TaskNavDestination

class TaskListPerformer(
    private val taskRepository: ITaskRepository,
    private val scopeCalculator: IScopeCalculator
): IActionPerformer {
    override suspend fun performAction(
        state: TaskAssistantState,
        action: Action,
        dispatchNewAction: (Action) -> Unit
    ) {
        if (state.session.screen !is TaskNavDestination.TaskList) return
        when(action) {
            is PerformInitialSetup -> dispatchNewAction(
                SelectNewScope(state.components.taskList.scope)
            )
            is DeleteTask -> taskRepository.deleteTask(action.task)
            is DeferTask -> {
                val nextScope = when(val oldScope = action.task.scope) {
                    null -> null
                    else -> scopeCalculator.add(oldScope, 1)
                }
                taskRepository.moveToNewScope(action.task, nextScope)
            }
            is RescheduleTask -> taskRepository.moveToNewScope(action.task, state.components.taskList.scope)
            is ToggleTaskComplete -> when(action.task.status) {
                TaskStatus.COMPLETE -> taskRepository.updateStatus(action.task, TaskStatus.INCOMPLETE)
                TaskStatus.INCOMPLETE -> {
                    taskRepository.moveToNewScope(action.task, scopeCalculator.generateScope())
                    taskRepository.updateStatus(action.task, TaskStatus.COMPLETE)
                }
            }
        }
    }
}