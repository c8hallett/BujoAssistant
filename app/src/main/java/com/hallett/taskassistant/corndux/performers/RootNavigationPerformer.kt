package com.hallett.taskassistant.corndux.performers

import com.hallett.corndux.Action
import com.hallett.corndux.Commit
import com.hallett.corndux.Init
import com.hallett.corndux.SideEffect
import com.hallett.logging.logI
import com.hallett.taskassistant.corndux.performers.actions.BottomNavigationClicked
import com.hallett.taskassistant.corndux.IPerformer
import com.hallett.taskassistant.corndux.performers.actions.FabClicked
import com.hallett.taskassistant.corndux.sideeffects.NavigateSingleTop
import com.hallett.taskassistant.corndux.sideeffects.NavigateToRootDestination
import com.hallett.taskassistant.corndux.TaskAssistantState
import com.hallett.taskassistant.corndux.performers.actions.PerformInitialSetup
import com.hallett.taskassistant.corndux.reducers.UpdateCurrentScreen

class RootNavigationPerformer: IPerformer {
    override suspend fun performAction(
        state: TaskAssistantState,
        action: Action,
        dispatchAction: suspend (Action) -> Unit,
        dispatchCommit: suspend (Commit) -> Unit,
        dispatchSideEffect: suspend (SideEffect) -> Unit
    ) {
        when(action) {
            is Init -> dispatchAction(PerformInitialSetup)
            is BottomNavigationClicked -> {
                dispatchCommit(UpdateCurrentScreen(action.destination))
                dispatchAction(PerformInitialSetup)
                dispatchSideEffect(NavigateToRootDestination(action.destination))
            }
            is FabClicked -> {
                dispatchCommit(UpdateCurrentScreen(action.destination))
                dispatchSideEffect(NavigateSingleTop(action.destination))
            }
        }
    }
}