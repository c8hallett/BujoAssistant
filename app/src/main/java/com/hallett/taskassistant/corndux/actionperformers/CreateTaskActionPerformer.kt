package com.hallett.taskassistant.corndux.actionperformers

import com.hallett.database.ITaskRepository
import com.hallett.taskassistant.corndux.CancelTask
import com.hallett.taskassistant.corndux.IActionPerformer
import com.hallett.taskassistant.corndux.NavigateUp
import com.hallett.taskassistant.corndux.SubmitTask
import com.hallett.taskassistant.corndux.TaskAssistantAction
import com.hallett.taskassistant.corndux.TaskAssistantSideEffect
import com.hallett.taskassistant.corndux.TaskAssistantState
import com.hallett.taskassistant.ui.navigation.TaskNavDestination

class CreateTaskActionPerformer(
    private val taskRepository: ITaskRepository,
): IActionPerformer {
    override suspend fun performAction(
        action: TaskAssistantAction,
        state: TaskAssistantState,
        dispatchNewAction: (TaskAssistantAction) -> Unit,
        dispatchSideEffect: (TaskAssistantSideEffect) -> Unit
    ): TaskAssistantState {
        if(state.session.screen !is TaskNavDestination.CreateTask) return state

        return when(action) {
            is SubmitTask -> {
                taskRepository.createNewTask(action.taskName, state.components.createTask.scope)
                dispatchSideEffect(NavigateUp)
                state
            }
            is CancelTask -> {
                dispatchSideEffect(NavigateUp)
                state
            }
            else -> state
        }

    }
}