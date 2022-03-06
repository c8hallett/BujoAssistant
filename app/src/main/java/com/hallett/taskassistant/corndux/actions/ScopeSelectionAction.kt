package com.hallett.taskassistant.corndux.actions

import com.hallett.corndux.Action
import com.hallett.scopes.model.Scope
import com.hallett.scopes.model.ScopeType

sealed class ScopeSelectionAction: Action
data class SelectNewScope(val newTaskScope: Scope?): ScopeSelectionAction()
data class SelectNewScopeType(val scopeType: ScopeType): ScopeSelectionAction()
object CancelScopeSelection: ScopeSelectionAction()
object EnterScopeSelection: ScopeSelectionAction()
