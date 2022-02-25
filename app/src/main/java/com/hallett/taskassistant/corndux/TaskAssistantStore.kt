package com.hallett.taskassistant.corndux

import com.hallett.corndux.ActionPerformer
import com.hallett.corndux.SideEffectPerformer
import com.hallett.corndux.Store
import kotlinx.coroutines.CoroutineScope

class TaskAssistantStore(
    initialState: TaskAssistantState,
    performers: List<ActionPerformer<TaskAssistantState, TaskAssistantAction, TaskAssistantSideEffect>>,
    sideEffectPerformer: SideEffectPerformer<TaskAssistantSideEffect>,
    scope: CoroutineScope,
): Store<TaskAssistantState, TaskAssistantAction, TaskAssistantSideEffect>(initialState, performers, sideEffectPerformer, scope)