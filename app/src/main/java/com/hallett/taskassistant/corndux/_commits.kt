package com.hallett.taskassistant.corndux

import androidx.paging.PagingData
import com.hallett.corndux.Commit
import com.hallett.domain.model.Task
import com.hallett.scopes.model.Scope
import com.hallett.scopes.model.ScopeType
import com.hallett.taskassistant.ui.model.ScopeSelectionInfo
import com.hallett.taskassistant.ui.model.TaskView
import kotlinx.coroutines.flow.Flow


data class UpdateSelectedScope(val scope: Scope?, val scopeSelectionInfo: ScopeSelectionInfo?) :
    Commit

data class UpdateScopeSelectionInfo(val scopeSelectionInfo: ScopeSelectionInfo?) : Commit
object ClearCreateTaskState : Commit

// probably could move to global commits
data class UpdateTypedTaskList(
    val scopeType: ScopeType,
    val taskList: Flow<PagingData<TaskView>>
) : Commit

data class UpdateExpandedTask(val task: Task) : Commit

data class UpdateTaskList(val taskList: Flow<PagingData<TaskView>>) : Commit

