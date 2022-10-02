package com.hallett.taskassistant.features.limboTasks.corndux

import androidx.paging.PagingData
import com.hallett.corndux.IState
import com.hallett.corndux.Store
import com.hallett.domain.model.Task
import com.hallett.taskassistant.features.genericTaskList.TaskActionsPerformer
import com.hallett.taskassistant.features.genericTaskList.TaskView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class LimboStore(
    limboPerformer: LimboPerformer,
    taskActionsPerformer: TaskActionsPerformer,
    limboReducer: LimboReducer,
    scope: CoroutineScope
) : Store<LimboState>(
    initialState = LimboState(),
    actors = listOf(limboPerformer, taskActionsPerformer, limboReducer),
    scope = scope
)

data class LimboState(
    val expandedTask: Task? = null,
    val list: Flow<PagingData<TaskView>> = flowOf(),
    val search: String = ""
) : IState
