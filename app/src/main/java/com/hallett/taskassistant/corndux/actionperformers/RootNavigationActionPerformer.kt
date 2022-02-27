package com.hallett.taskassistant.corndux.actionperformers

import com.hallett.taskassistant.corndux.BottomNavigationClicked
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
                    state.copy(screen = action.destination)
                }
                is OverdueTasksClicked -> {
                    dispatchNewAction(PerformInitialSetup)
                    state.copy(screen = action.destination)
                }
            }
        }
        is FabClicked -> {
            dispatchSideEffect(NavigateSingleTop(action.destination))
            state.copy(scope = null, screen = action.destination)
        }
        else -> state
    }
}