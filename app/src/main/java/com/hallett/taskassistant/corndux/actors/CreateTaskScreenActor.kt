package com.hallett.taskassistant.corndux.actors

import com.hallett.corndux.Action
import com.hallett.corndux.SideEffect
import com.hallett.database.ITaskRepository
import com.hallett.taskassistant.corndux.IActionPerformer
import com.hallett.taskassistant.corndux.IReducer
import com.hallett.taskassistant.corndux.TaskAssistantState
import com.hallett.taskassistant.corndux.actions.CancelTask
import com.hallett.taskassistant.corndux.actions.SubmitTask
import com.hallett.taskassistant.corndux.sideeffects.NavigateUp
import com.hallett.taskassistant.ui.navigation.TaskNavDestination

class CreateTaskScreenActor(
    private val taskRepository: ITaskRepository,
    ): IActionPerformer, IReducer {

    // TODO: no error handling lmaoo
    override suspend fun performAction(
        state: TaskAssistantState,
        action: Action,
        dispatchNewAction: (Action) -> Unit,
    ) {
        if(state.session.screen is TaskNavDestination.CreateTask) {
            when(action) {
                is SubmitTask -> taskRepository.createNewTask(action.taskName, state.components.createTask.scope)
            }
        }
    }

    override fun reduce(
        state: TaskAssistantState,
        action: Action,
        dispatchSideEffect: (SideEffect) -> Unit
    ): TaskAssistantState {
        if(state.session.screen is TaskNavDestination.CreateTask) {
            when(action) {
                is SubmitTask -> dispatchSideEffect(NavigateUp)
                is CancelTask -> dispatchSideEffect(NavigateUp)
            }
        }
        return state
    }
}