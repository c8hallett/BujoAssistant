package com.hallett.taskassistant.mainNavigation.corndux

import androidx.navigation.NavController
import com.hallett.corndux.Action
import com.hallett.corndux.Commit
import com.hallett.corndux.Performer
import com.hallett.corndux.SideEffect
import com.hallett.taskassistant.corndux.performers.actions.BottomNavigationClicked
import com.hallett.taskassistant.corndux.performers.actions.FabClicked
import com.hallett.taskassistant.corndux.sideeffects.NavigateSingleTop
import com.hallett.taskassistant.corndux.sideeffects.NavigateToRootDestination

class NavigationPerformer: Performer {
    override suspend fun performAction(
        action: Action,
        dispatchAction: suspend (Action) -> Unit,
        dispatchCommit: suspend (Commit) -> Unit,
        dispatchSideEffect: suspend (SideEffect) -> Unit
    ) {
        when (action) {
            is BottomNavigationClicked -> dispatchSideEffect(
                NavigateToRootDestination(action.destination)
            )
            is FabClicked -> dispatchSideEffect(
                NavigateSingleTop(action.destination)
            )
        }
    }
}