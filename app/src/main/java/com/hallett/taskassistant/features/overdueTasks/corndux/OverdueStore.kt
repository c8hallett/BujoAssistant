package com.hallett.taskassistant.features.overdueTasks.corndux

import androidx.paging.PagingData
import com.hallett.corndux.IState
import com.hallett.corndux.Store
import com.hallett.domain.model.Task
import com.hallett.taskassistant.ui.model.TaskView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class OverdueStore(
    overduePerformer: OverduePerformer,
    overdueReducer: OverdueReducer,
    scope: CoroutineScope
) : Store<OverdueState>(
    initialState = OverdueState(),
    actors = listOf(overduePerformer, overdueReducer),
    scope = scope
)

data class OverdueState(
    val currentlyExpandedTask: Task? = null,
    val taskList: Flow<PagingData<TaskView>> = flowOf(),
) : IState