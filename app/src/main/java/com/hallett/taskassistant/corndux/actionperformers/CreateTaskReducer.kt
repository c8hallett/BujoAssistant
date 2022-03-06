package com.hallett.taskassistant.corndux.actionperformers

import com.hallett.corndux.Action
import com.hallett.corndux.SideEffect
import com.hallett.database.ITaskRepository
import com.hallett.taskassistant.corndux.actions.CancelTask
import com.hallett.taskassistant.corndux.IActionPerformer
import com.hallett.taskassistant.corndux.sideeffects.NavigateUp
import com.hallett.taskassistant.corndux.actions.SubmitTask
import com.hallett.taskassistant.corndux.TaskAssistantState
import com.hallett.taskassistant.ui.navigation.TaskNavDestination

class CreateTaskReducer(
    private val taskRepository: ITaskRepository,
): IActionPerformer {
    override suspend fun performAction(
        action: Action,
        state: TaskAssistantState,
        dispatchNewAction: (Action) -> Unit,
        dispatchSideEffect: (SideEffect) -> Unit
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