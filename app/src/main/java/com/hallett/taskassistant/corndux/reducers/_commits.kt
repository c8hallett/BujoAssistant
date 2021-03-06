package com.hallett.taskassistant.corndux.reducers

import androidx.paging.PagingData
import com.hallett.corndux.Commit
import com.hallett.domain.model.Task
import com.hallett.scopes.model.Scope
import com.hallett.scopes.model.ScopeType
import com.hallett.taskassistant.corndux.FutureTaskListState
import com.hallett.taskassistant.corndux.ScopeSelectionInfo
import com.hallett.taskassistant.ui.model.TaskView
import com.hallett.taskassistant.ui.navigation.TaskNavDestination
import kotlinx.coroutines.flow.Flow

data class UpdateDashboardTaskList(
    val scopeType: ScopeType,
    val taskList: Flow<PagingData<TaskView>>
) : Commit

data class UpdateCreateTaskSelectedScope(val scope: Scope?) : Commit
data class UpdateCreateTaskScopeSelectionInfo(val scopeSelectionInfo: ScopeSelectionInfo?) : Commit
object ClearCreateTaskState : Commit

data class UpdateOverdueTaskList(val taskList: Flow<PagingData<TaskView>>) : Commit

data class UpdateTaskListTaskList(val taskList: Flow<PagingData<TaskView>>) : Commit
data class UpdateTaskListSelectedScope(val scope: Scope?) : Commit
data class UpdateTaskListScopeSelectionInfo(val scopeSelectionInfo: ScopeSelectionInfo?) : Commit

data class UpdateTaskListExpandedTask(val task: Task?) : Commit
data class UpdateDashboardExpandedTask(val task: Task?) : Commit
data class UpdateOverdueExpandedTask(val task: Task?) : Commit
data class UpdateFutureExpandedTask(val task: Task?) : Commit

data class UpdateCurrentScreen(val screen: TaskNavDestination) : Commit

data class UpdateFutureTaskLists(
    val scheduled: Flow<PagingData<TaskView>>,
    val unscheduled: Flow<PagingData<TaskView>>
) : Commit

data class UpdateCurrentlyExpandedList(val list: FutureTaskListState.ExpandedList) : Commit