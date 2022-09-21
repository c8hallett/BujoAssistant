package com.hallett.taskassistant.main.corndux

import com.hallett.corndux.Action
import com.hallett.corndux.Reducer
import com.hallett.corndux.SideEffect
import com.hallett.corndux.StatefulPerformer
import com.hallett.logging.logI
import com.hallett.taskassistant.main.TaskNavDestination

class NavigationActor : StatefulPerformer<GlobalState>, Reducer<GlobalState> {
    override fun performAction(
        state: GlobalState,
        action: Action,
        dispatchAction: (Action) -> Unit,
        dispatchSideEffect: (SideEffect) -> Unit
    ) {
        when (action) {
            is ClickBottomNavigation -> dispatchSideEffect(
                NavigateToRootDestination(action.destination)
            )
            is ClickFab -> dispatchSideEffect(
                NavigateSingleTop(action.destination)
            )
        }
    }

    override fun reduce(state: GlobalState, action: Action): GlobalState {
        return when (action) {
            is NavigateToNewDestination -> state.copy(
                shouldShowFab = action.destination.route != TaskNavDestination.CreateTask.route
            ).also { logI("Route = ${action.destination.route}") }
            else -> state
        }
    }
}