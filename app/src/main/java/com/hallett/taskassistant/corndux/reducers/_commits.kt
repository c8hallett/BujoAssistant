package com.hallett.taskassistant.corndux.reducers

import androidx.paging.PagingData
import com.hallett.corndux.Commit
import com.hallett.domain.model.Task
import com.hallett.scopes.model.Scope
import com.hallett.scopes.model.ScopeType
import com.hallett.taskassistant.corndux.ScopeSelectionInfo
import com.hallett.taskassistant.ui.navigation.TaskNavDestination
import kotlinx.coroutines.flow.Flow

data class UpdateDashboardTaskList(val scopeType: ScopeType, val taskList: Flow<PagingData<Task>>): Commit

data class UpdateCreateTaskSelectedScope(val scope: Scope?): Commit
data class UpdateCreateTaskScopeSelectionInfo(val scopeSelectionInfo: ScopeSelectionInfo?): Commit
object ClearCreateTaskState: Commit

data class UpdateOverdueTaskList(val taskList: Flow<PagingData<Task>>): Commit

data class UpdateTaskListTaskList(val taskList: Flow<PagingData<Task>>): Commit
data class UpdateTaskListSelectedScope(val scope: Scope?): Commit
data class UpdateTaskListScopeSelectionInfo(val scopeSelectionInfo: ScopeSelectionInfo?): Commit
data class UpdateTaskListCurrentlySelectedTask(val task: Task?): Commit

data class UpdateCurrentScreen(val screen: TaskNavDestination): Commit