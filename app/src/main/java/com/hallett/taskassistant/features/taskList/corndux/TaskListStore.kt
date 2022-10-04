package com.hallett.taskassistant.features.taskList.corndux

import androidx.paging.PagingData
import com.hallett.corndux.IState
import com.hallett.corndux.Store
import com.hallett.domain.model.Task
import com.hallett.scopes.model.Scope
import com.hallett.taskassistant.ui.genericTaskList.TaskActionsPerformer
import com.hallett.taskassistant.ui.genericTaskList.TaskView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class TaskListStore(
    taskListPerformer: TaskListPerformer,
    taskActionsPerformer: TaskActionsPerformer,
    taskListReducer: TaskListReducer,
    scope: CoroutineScope,
) : Store<TaskListState>(
    initialState = TaskListState(),
    actors = listOf(taskListPerformer, taskActionsPerformer, taskListReducer),
    scope = scope
)

data class TaskListState(
    val currentlyExpandedTask: Task? = null,
    val taskList: Flow<PagingData<TaskView>> = flowOf(),
    val scope: Scope? = null,
) : IState