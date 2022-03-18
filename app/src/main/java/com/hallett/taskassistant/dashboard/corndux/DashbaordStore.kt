package com.hallett.taskassistant.dashboard.corndux

import androidx.paging.PagingData
import com.hallett.corndux.Actor
import com.hallett.corndux.IState
import com.hallett.corndux.Store
import com.hallett.domain.model.Task
import com.hallett.scopes.model.ScopeType
import com.hallett.taskassistant.ui.model.TaskView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class DashboardStore(
    initialState: DashboardState = DashboardState(),
    actors: List<Actor<out DashboardState>>,
    scope: CoroutineScope
) : Store<DashboardState>(
    initialState,
    actors,
    scope
)

data class DashboardState(
    val currentlyExpandedTask: Task? = null,
    val taskList: Flow<PagingData<TaskView>> = flowOf(),
    val scopeType: ScopeType = ScopeType.DAY
) : IState


