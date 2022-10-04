package com.hallett.taskassistant.features.overdueTasks.corndux

import androidx.paging.PagingData
import com.hallett.corndux.IState
import com.hallett.corndux.Store
import com.hallett.domain.model.Task
import com.hallett.taskassistant.ui.genericTaskList.TaskActionsPerformer
import com.hallett.taskassistant.ui.genericTaskList.TaskView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class OverdueStore(
    overduePerformer: OverduePerformer,
    taskActionsPerformer: TaskActionsPerformer,
    overdueReducer: OverdueReducer,
    scope: CoroutineScope
) : Store<OverdueState>(
    initialState = OverdueState(),
    actors = listOf(overduePerformer, taskActionsPerformer, overdueReducer),
    scope = scope
)

data class OverdueState(
    val currentlyExpandedTask: Task? = null,
    val currentlySchedulingTask: Task? = null,
    val taskList: Flow<PagingData<TaskView>> = flowOf(),
) : IState