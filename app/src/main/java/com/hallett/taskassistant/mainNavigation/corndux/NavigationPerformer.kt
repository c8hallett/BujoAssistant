package com.hallett.taskassistant.mainNavigation.corndux

import com.hallett.corndux.Action
import com.hallett.corndux.SideEffect
import com.hallett.corndux.StatelessPerformer
import com.hallett.taskassistant.corndux.BottomNavigationClicked
import com.hallett.taskassistant.corndux.FabClicked
import com.hallett.taskassistant.corndux.NavigateSingleTop
import com.hallett.taskassistant.corndux.NavigateToRootDestination

class NavigationPerformer : StatelessPerformer {
    override fun performAction(
        action: Action,
        dispatchAction: (Action) -> Unit,
        dispatchSideEffect: (SideEffect) -> Unit
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