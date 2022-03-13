package com.hallett.taskassistant.corndux.performers

import com.hallett.corndux.Action
import com.hallett.corndux.Commit
import com.hallett.corndux.SideEffect
import com.hallett.taskassistant.corndux.IPerformer
import com.hallett.taskassistant.corndux.TaskAssistantState
import com.hallett.taskassistant.corndux.performers.actions.BottomNavigationClicked
import com.hallett.taskassistant.corndux.performers.actions.FabClicked
import com.hallett.taskassistant.corndux.reducers.ClearCreateTaskState
import com.hallett.taskassistant.corndux.reducers.UpdateCurrentScreen
import com.hallett.taskassistant.corndux.sideeffects.NavigateSingleTop
import com.hallett.taskassistant.corndux.sideeffects.NavigateToRootDestination

class RootNavigationPerformer : IPerformer {
    override suspend fun performAction(
        state: TaskAssistantState,
        action: Action,
        dispatchAction: suspend (Action) -> Unit,
        dispatchCommit: suspend (Commit) -> Unit,
        dispatchSideEffect: suspend (SideEffect) -> Unit
    ) {
        when (action) {
            is BottomNavigationClicked -> {
                dispatchCommit(UpdateCurrentScreen(action.destination))
                dispatchSideEffect(NavigateToRootDestination(action.destination))
            }
            is FabClicked -> {
                dispatchCommit(UpdateCurrentScreen(action.destination))
                dispatchCommit(ClearCreateTaskState)
                dispatchSideEffect(NavigateSingleTop(action.destination))
            }
        }
    }
}