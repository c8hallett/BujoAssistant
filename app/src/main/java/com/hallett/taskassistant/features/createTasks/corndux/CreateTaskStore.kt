package com.hallett.taskassistant.features.createTasks.corndux

import com.hallett.corndux.IState
import com.hallett.corndux.Store
import com.hallett.domain.model.Task
import com.hallett.domain.model.TaskStatus
import com.hallett.scopes.model.Scope
import com.hallett.taskassistant.features.scopeSelection.ScopeSelectionInfo
import kotlinx.coroutines.CoroutineScope

class CreateTaskStore(
    createTaskPerformer: CreateTaskPerformer,
    createTaskReducer: CreateTaskReducer,
    scope: CoroutineScope
) : Store<CreateTaskState>(
    initialState = CreateTaskState(),
    actors = listOf(createTaskPerformer, createTaskReducer),
    scope
)

data class CreateTaskState(
    val task: Task = Task(0L, "", null, TaskStatus.INCOMPLETE),
    val scopeSelectionInfo: ScopeSelectionInfo? = null
) : IState