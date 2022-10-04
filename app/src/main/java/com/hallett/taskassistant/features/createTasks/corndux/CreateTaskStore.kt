package com.hallett.taskassistant.features.createTasks.corndux

import com.hallett.corndux.IState
import com.hallett.corndux.Store
import com.hallett.domain.model.TaskStatus
import com.hallett.scopes.model.Scope
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
    val taskId: Long = 0L,
    val taskName: String = "",
    val taskScope: Scope? = null,
    val taskStatus: TaskStatus = TaskStatus.INCOMPLETE,
) : IState