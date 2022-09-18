package com.hallett.taskassistant.mainNavigation.corndux

import com.hallett.corndux.IState
import com.hallett.corndux.Store
import kotlinx.coroutines.CoroutineScope

class GlobalStore(
    navigationPerformer: NavigationPerformer,
    scope: CoroutineScope,
) : Store<GlobalState>(
    initialState = GlobalState,
    actors = listOf(navigationPerformer),
    scope = scope)

object GlobalState : IState