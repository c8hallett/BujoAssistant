package com.hallett.taskassistant.features.taskList.corndux

import androidx.paging.PagingData
import com.hallett.corndux.IState
import com.hallett.corndux.Store
import com.hallett.domain.model.Task
import com.hallett.scopes.model.Scope
import com.hallett.taskassistant.features.genericTaskList.TaskView
import com.hallett.taskassistant.features.scopeSelection.ScopeSelectionInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class TaskListStore(
    taskListPerformer: TaskListPerformer,
    taskListReducer: TaskListReducer,
    scope: CoroutineScope,
) : Store<TaskListState>(
    initialState = TaskListState(),
    actors = listOf(taskListPerformer, taskListReducer),
    scope = scope
)

data class TaskListState(
    val currentlyExpandedTask: Task? = null,
    val taskList: Flow<PagingData<TaskView>> = flowOf(),
    val scope: Scope? = null,
    val scopeSelectionInfo: ScopeSelectionInfo? = null
) : IState