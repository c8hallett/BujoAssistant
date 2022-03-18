package com.hallett.taskassistant.mainNavigation.corndux

import com.hallett.corndux.Actor
import com.hallett.corndux.IState
import com.hallett.corndux.Store
import kotlinx.coroutines.CoroutineScope

class NavigationStore(
    actors: List<Actor<out NavigationState>>,
    scope: CoroutineScope
): Store<NavigationState>(NavigationState, actors, scope)

object NavigationState: IState