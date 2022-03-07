package com.hallett.taskassistant.corndux.performers.actions

import com.hallett.corndux.Action
import com.hallett.scopes.model.Scope
import com.hallett.scopes.model.ScopeType

data class SelectNewScope(val newTaskScope: Scope?): Action
data class SelectNewScopeType(val scopeType: ScopeType): Action
object CancelScopeSelection: Action
object EnterScopeSelection: Action
