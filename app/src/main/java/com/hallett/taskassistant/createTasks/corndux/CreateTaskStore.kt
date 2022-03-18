package com.hallett.taskassistant.createTasks.corndux

import com.hallett.corndux.Action
import com.hallett.corndux.Actor
import com.hallett.corndux.Commit
import com.hallett.corndux.IState
import com.hallett.corndux.Store
import com.hallett.scopes.model.Scope
import com.hallett.taskassistant.corndux.ScopeSelectionInfo
import kotlinx.coroutines.CoroutineScope

class CreateTaskStore(
    initialState: CreateTaskState = CreateTaskState(),
    actors: List<Actor<out CreateTaskState>>,
    scope: CoroutineScope
): Store<CreateTaskState>(initialState, actors, scope)

data class CreateTaskState(
    val taskName: String = "",
    val scope: Scope? = null,
    val scopeSelectionInfo: ScopeSelectionInfo? = null): IState


data class SubmitTask(val taskName: String) : Action
object CancelTask : Action

data class UpdateSelectedScope(val scope: Scope?, val scopeSelectionInfo: ScopeSelectionInfo?): Commit
data class UpdateScopeSelectionInfo(val scopeSelectionInfo: ScopeSelectionInfo?): Commit
object ClearCreateTaskState: Commit