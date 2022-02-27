package com.hallett.taskassistant.corndux

import com.hallett.corndux.ActionPerformer
import com.hallett.corndux.Middleware
import com.hallett.corndux.SideEffectPerformer
import com.hallett.corndux.Store
import kotlinx.coroutines.CoroutineScope

class TaskAssistantStore(
    initialState: TaskAssistantState,
    performers: List<IActionPerformer>,
    middlewares: List<IMiddleware>,
    sideEffectPerformer: ISideEffectPerformer,
    scope: CoroutineScope,
): IStore(initialState, performers, middlewares, sideEffectPerformer, scope)

typealias IStore = Store<TaskAssistantState, TaskAssistantAction, TaskAssistantSideEffect>
typealias IActionPerformer = ActionPerformer<TaskAssistantState, TaskAssistantAction, TaskAssistantSideEffect>
typealias IMiddleware = Middleware<TaskAssistantState, TaskAssistantAction>
typealias ISideEffectPerformer = SideEffectPerformer<TaskAssistantSideEffect>