package com.hallett.taskassistant.taskList.corndux

import androidx.paging.PagingData
import com.hallett.corndux.Actor
import com.hallett.corndux.IState
import com.hallett.corndux.Store
import com.hallett.domain.model.Task
import com.hallett.scopes.model.Scope
import com.hallett.taskassistant.ui.model.ScopeSelectionInfo
import com.hallett.taskassistant.ui.model.TaskView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class TaskListStore(
    initialState: TaskListState = TaskListState(),
    actors: List<Actor<out TaskListState>>,
    scope: CoroutineScope,
) : Store<TaskListState>(initialState, actors, scope)

data class TaskListState(
    val currentlyExpandedTask: Task? = null,
    val taskList: Flow<PagingData<TaskView>> = flowOf(),
    val scope: Scope? = null,
    val scopeSelectionInfo: ScopeSelectionInfo? = null
) : IState