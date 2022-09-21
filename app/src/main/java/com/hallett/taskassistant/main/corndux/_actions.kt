package com.hallett.taskassistant.main.corndux

import androidx.navigation.NavDestination
import androidx.paging.PagingData
import com.hallett.corndux.Action
import com.hallett.domain.model.Task
import com.hallett.scopes.model.Scope
import com.hallett.scopes.model.ScopeType
import com.hallett.taskassistant.features.genericTaskList.TaskView
import com.hallett.taskassistant.features.scopeSelection.ScopeSelectionInfo
import kotlinx.coroutines.flow.Flow

sealed interface NavigationAction : Action {
    val destination: String
}

data class ClickFab(override val destination: String) : NavigationAction
data class ClickBottomNavigation(override val destination: String) : NavigationAction

data class NavigateToNewDestination(val destination: NavDestination) : Action


data class OpenTask(val taskId: Long?) : Action
data class SubmitTask(val taskName: String) : Action
data class DisplayTaskForEdit(val task: Task): Action
object CancelTask : Action
object ClearCreateTaskState : Action

object LoadLargerScope : Action
object LoadSmallerScope : Action

object AddRandomOverdueTask : Action

data class UpdateSelectedScope(val scope: Scope?, val scopeSelectionInfo: ScopeSelectionInfo?) :
    Action

data class UpdateScopeSelectionInfo(val scopeSelectionInfo: ScopeSelectionInfo?) : Action

// probably could move to global commits
data class UpdateTypedTaskList(
    val scopeType: ScopeType,
    val taskList: Flow<PagingData<TaskView>>
) : Action

data class UpdateExpandedTask(val task: Task) : Action

data class UpdateTaskList(val taskList: Flow<PagingData<TaskView>>) : Action
