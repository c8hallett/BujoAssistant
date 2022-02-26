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
): IStore(initialState, performers, sideEffectPerformer, scope)


typealias IActionPerformer = ActionPerformer<TaskAssistantState, TaskAssistantAction, TaskAssistantSideEffect>
typealias IStore = Store<TaskAssistantState, TaskAssistantAction, TaskAssistantSideEffect>