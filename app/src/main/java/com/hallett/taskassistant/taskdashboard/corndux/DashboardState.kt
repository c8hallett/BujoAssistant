package com.hallett.taskassistant.taskdashboard.corndux

import androidx.paging.PagingData
import com.hallett.corndux.IState
import com.hallett.domain.model.Task
import com.hallett.scopes.model.ScopeType
import kotlinx.coroutines.flow.Flow

data class DashboardState(
    val taskList: Flow<PagingData<Task>>,
    val scopeType: ScopeType // eventually make this nullable?
): IState