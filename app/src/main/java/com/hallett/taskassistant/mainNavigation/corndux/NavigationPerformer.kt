package com.hallett.taskassistant.mainNavigation.corndux

import com.hallett.corndux.Action
import com.hallett.corndux.SideEffect
import com.hallett.corndux.StatelessPerformer

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