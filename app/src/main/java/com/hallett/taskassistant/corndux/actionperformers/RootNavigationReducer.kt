package com.hallett.taskassistant.corndux.actionperformers

import com.hallett.corndux.Action
import com.hallett.corndux.SideEffect
import com.hallett.taskassistant.corndux.actions.BottomNavigationClicked
import com.hallett.taskassistant.corndux.CreateTaskState
import com.hallett.taskassistant.corndux.actions.DashboardClicked
import com.hallett.taskassistant.corndux.actions.FabClicked
import com.hallett.taskassistant.corndux.IActionPerformer
import com.hallett.taskassistant.corndux.sideeffects.NavigateSingleTop
import com.hallett.taskassistant.corndux.sideeffects.NavigateToRootDestination
import com.hallett.taskassistant.corndux.actions.OverdueTasksClicked
import com.hallett.taskassistant.corndux.actions.PerformInitialSetup
import com.hallett.taskassistant.corndux.TaskAssistantState
import com.hallett.taskassistant.corndux.actions.TaskListClicked
import com.hallett.taskassistant.ui.navigation.TaskNavDestination

class RootNavigationReducer: IActionPerformer {
    override suspend fun performAction(
        action: Action,
        state: TaskAssistantState,
        dispatchNewAction: (Action) -> Unit,
        dispatchSideEffect: (SideEffect) -> Unit
    ): TaskAssistantState = when(action) {
        is BottomNavigationClicked -> {
            dispatchSideEffect(NavigateToRootDestination(action.destination))
            when(action) {
                is TaskListClicked -> {
                    dispatchNewAction(PerformInitialSetup)
                    state.updateDestination(action.destination)
                }
                is OverdueTasksClicked -> {
                    dispatchNewAction(PerformInitialSetup)
                    state.updateDestination(action.destination)
                }
                is DashboardClicked -> {
                    dispatchNewAction(PerformInitialSetup)
                    state.updateDestination(action.destination)
                }
            }
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