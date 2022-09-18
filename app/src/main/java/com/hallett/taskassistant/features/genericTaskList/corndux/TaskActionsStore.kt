package com.hallett.taskassistant.features.genericTaskList.corndux

import com.hallett.corndux.IState
import com.hallett.corndux.Store
import kotlinx.coroutines.CoroutineScope

class TaskActionsStore(
    taskActionsPerformer: TaskActionsPerformer,
    scope: CoroutineScope
) : Store<IState>(
    initialState = object: IState{},
    actors = listOf(taskActionsPerformer),
    scope = scope
)