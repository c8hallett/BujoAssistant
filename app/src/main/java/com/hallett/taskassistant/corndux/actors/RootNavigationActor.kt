package com.hallett.taskassistant.corndux.actors

import com.hallett.corndux.Action
import com.hallett.corndux.SideEffect
import com.hallett.taskassistant.corndux.actions.BottomNavigationClicked
import com.hallett.taskassistant.corndux.CreateTaskState
import com.hallett.taskassistant.corndux.IActionPerformer
import com.hallett.taskassistant.corndux.actions.FabClicked
import com.hallett.taskassistant.corndux.IReducer
import com.hallett.taskassistant.corndux.sideeffects.NavigateSingleTop
import com.hallett.taskassistant.corndux.sideeffects.NavigateToRootDestination
import com.hallett.taskassistant.corndux.actions.PerformInitialSetup
import com.hallett.taskassistant.corndux.TaskAssistantState
import com.hallett.taskassistant.ui.navigation.TaskNavDestination

class RootNavigationActor: IReducer, IActionPerformer {
    override suspend fun performAction(
        state: TaskAssistantState,
        action: Action,
        dispatchNewAction: (Action) -> Unit
    ) {
        when(action) {
            is BottomNavigationClicked -> dispatchNewAction(PerformInitialSetup)
        }
    }

    override fun reduce(
        state: TaskAssistantState,
        action: Action,
        dispatchSideEffect: (SideEffect) -> Unit
    ): TaskAssistantState = when(action) {
        is BottomNavigationClicked -> {
            dispatchSideEffect(NavigateToRootDestination(action.destination))
            state.updateDestination(action.destination)
        }
        is FabClicked -> {
            dispatchSideEffect(NavigateSingleTop(action.destination))
            state.updateDestination(action.destination).clearCreateTaskState()
        }
        else -> state
    }

    private fun TaskAssistantState.updateDestination(destination: TaskNavDestination): TaskAssistantState {
        return updateSession { copy(screen = destination) }
    }

    private fun TaskAssistantState.clearCreateTaskState(): TaskAssistantState {
        return updateComponents { copy(createTask = CreateTaskState()) }
    }
}