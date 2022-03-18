package com.hallett.taskassistant.overdueTasks.corndux

import androidx.paging.PagingData
import com.hallett.corndux.Actor
import com.hallett.corndux.IState
import com.hallett.corndux.Store
import com.hallett.domain.model.Task
import com.hallett.taskassistant.ui.model.TaskView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class OverdueStore(
    initialState: OverdueState = OverdueState(),
    actors: List<Actor<out OverdueState>>,
    scope: CoroutineScope
) : Store<OverdueState>(
    initialState,
    actors,
    scope
)

data class OverdueState(
    val currentlyExpandedTask: Task? = null,
    val taskList: Flow<PagingData<TaskView>> = flowOf(),
) : IState