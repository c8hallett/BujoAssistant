package com.hallett.taskassistant.corndux

import com.hallett.corndux.IAction
import com.hallett.scopes.model.Scope
import com.hallett.scopes.model.ScopeType

sealed interface TaskAssistantAction: IAction
data class UpdateTaskName(val newTaskName: String): TaskAssistantAction
data class UpdateTaskScope(val newTaskScope: Scope?): TaskAssistantAction
data class SelectNewScopeType(val scopeType: ScopeType): TaskAssistantAction
object ScopeSelectionCancelled: TaskAssistantAction
object EnterScopeSelection: TaskAssistantAction
object SubmitTask: TaskAssistantAction
object CancelTask: TaskAssistantAction