package com.hallett.taskassistant.corndux.actionperformers

import com.hallett.taskassistant.corndux.BottomNavigationClicked
import com.hallett.taskassistant.corndux.Components
import com.hallett.taskassistant.corndux.CreateTaskState
import com.hallett.taskassistant.corndux.DashboardClicked
import com.hallett.taskassistant.corndux.FabClicked
import com.hallett.taskassistant.corndux.IActionPerformer
import com.hallett.taskassistant.corndux.NavigateSingleTop
import com.hallett.taskassistant.corndux.NavigateToRootDestination
import com.hallett.taskassistant.corndux.OverdueTasksClicked
import com.hallett.taskassistant.corndux.PerformInitialSetup
import com.hallett.taskassistant.corndux.TaskAssistantAction
import com.hallett.taskassistant.corndux.TaskAssistantSideEffect
import com.hallett.taskassistant.corndux.TaskAssistantState
import com.hallett.taskassistant.corndux.TaskListClicked
import com.hallett.taskassistant.ui.navigation.TaskNavDestination

class RootNavigationActionPerformer(): IActionPerformer {
    override suspend fun performAction(
        action: TaskAssistantAction,
        state: TaskAssistantState,
        dispatchNewAction: (TaskAssistantAction) -> Unit,
        dispatchSideEffect: (TaskAssistantSideEffect) -> Unit
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