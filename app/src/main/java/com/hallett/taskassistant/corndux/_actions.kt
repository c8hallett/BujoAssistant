package com.hallett.taskassistant.corndux

import androidx.paging.PagingData
import com.hallett.corndux.Action
import com.hallett.domain.model.Task
import com.hallett.scopes.model.Scope
import com.hallett.scopes.model.ScopeType
import com.hallett.taskassistant.mainNavigation.TaskNavDestination
import com.hallett.taskassistant.ui.model.ScopeSelectionInfo
import com.hallett.taskassistant.ui.model.TaskView
import kotlinx.coroutines.flow.Flow

sealed class NavigationAction(val destination: TaskNavDestination) : Action
class FabClicked(destination: TaskNavDestination) : NavigationAction(destination)
class BottomNavigationClicked(destination: TaskNavDestination) : NavigationAction(destination)

data class SubmitTask(val taskName: String) : Action
object CancelTask : Action

object LoadLargerScope : Action
object LoadSmallerScope : Action

object AddRandomOverdueTask : Action

data class UpdateSelectedScope(val scope: Scope?, val scopeSelectionInfo: ScopeSelectionInfo?) : Action

data class UpdateScopeSelectionInfo(val scopeSelectionInfo: ScopeSelectionInfo?) : Action
object ClearCreateTaskState : Action

// probably could move to global commits
data class UpdateTypedTaskList(
    val scopeType: ScopeType,
    val taskList: Flow<PagingData<TaskView>>
) : Action

data class UpdateExpandedTask(val task: Task) : Action

data class UpdateTaskList(val taskList: Flow<PagingData<TaskView>>) : Action
