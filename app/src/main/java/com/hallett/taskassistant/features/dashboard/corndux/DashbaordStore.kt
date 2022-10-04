package com.hallett.taskassistant.features.dashboard.corndux

import androidx.paging.PagingData
import com.hallett.corndux.IState
import com.hallett.corndux.Store
import com.hallett.domain.model.Task
import com.hallett.scopes.model.ScopeType
import com.hallett.taskassistant.ui.genericTaskList.TaskActionsPerformer
import com.hallett.taskassistant.ui.genericTaskList.TaskView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class DashboardStore(
    dashboardPerformer: DashboardPerformer,
    taskActionsPerformer: TaskActionsPerformer,
    dashboardReducer: DashboardReducer,
    scope: CoroutineScope
) : Store<DashboardState>(
    initialState = DashboardState(),
    actors = listOf(dashboardPerformer, taskActionsPerformer, dashboardReducer),
    scope = scope
)

data class DashboardState(
    val currentlyExpandedTask: Task? = null,
    val currentlySchedulingTask: Task? = null,
    val taskList: Flow<PagingData<TaskView>> = flowOf(),
    val scopeType: ScopeType = ScopeType.DAY,
) : IState


