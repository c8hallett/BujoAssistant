package com.hallett.taskassistant.corndux.interpreters

import com.hallett.corndux.Event
import com.hallett.domain.model.Task
import com.hallett.scopes.model.Scope
import com.hallett.scopes.model.ScopeType

sealed interface ScopeSelectionEvent: Event
    data class NewScopeClicked(val newTaskScope: Scope?) : ScopeSelectionEvent
    data class NewScopeTypeClicked(val scopeType: ScopeType) : ScopeSelectionEvent
    object ScopeSelectionCancelled : ScopeSelectionEvent
    object ScopeSelectionEntered : ScopeSelectionEvent

data class TaskInListClicked(val task: Task): Event
