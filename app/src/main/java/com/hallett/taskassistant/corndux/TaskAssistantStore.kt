package com.hallett.taskassistant.corndux

import com.hallett.corndux.Reducer
import com.hallett.corndux.Middleware
import com.hallett.corndux.SideEffectPerformer
import com.hallett.corndux.Store
import kotlinx.coroutines.CoroutineScope

class TaskAssistantStore(
    initialState: TaskAssistantState,
    performers: List<IActionPerformer>,
    middlewares: List<IMiddleware>,
    sideEffectPerformers: List<SideEffectPerformer> = listOf(),
    scope: CoroutineScope,
): IStore(initialState, performers, middlewares, sideEffectPerformers, scope)

typealias IStore = Store<TaskAssistantState>
typealias IActionPerformer = Reducer<TaskAssistantState>
typealias IMiddleware = Middleware<TaskAssistantState>