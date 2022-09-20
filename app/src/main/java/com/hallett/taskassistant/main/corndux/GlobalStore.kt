package com.hallett.taskassistant.main.corndux

import com.hallett.corndux.IState
import com.hallett.corndux.Store
import kotlinx.coroutines.CoroutineScope

class GlobalStore(
    navigationActor: NavigationActor,
    scope: CoroutineScope,
) : Store<GlobalState>(
    initialState = GlobalState(),
    actors = listOf(navigationActor),
    scope = scope
)

data class GlobalState(
    val shouldShowFab: Boolean = true
) : IState