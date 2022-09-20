package com.hallett.taskassistant.main.corndux

import androidx.navigation.NavDestination
import androidx.paging.PagingData
import com.hallett.corndux.Action
import com.hallett.domain.model.Task
import com.hallett.scopes.model.Scope
import com.hallett.scopes.model.ScopeType
import com.hallett.taskassistant.features.genericTaskList.TaskView
import com.hallett.taskassistant.features.scopeSelection.ScopeSelectionInfo
import com.hallett.taskassistant.main.TaskNavDestination
import kotlinx.coroutines.flow.Flow

sealed interface NavigationAction : Action {
    val destination: TaskNavDestination
}

data class ClickFab(override val destination: TaskNavDestination) : NavigationAction
data class ClickBottomNavigation(override val destination: TaskNavDestination) : NavigationAction
data class NavigateToNewDestination(val destination: NavDestination) : Action


data class SubmitTask(val taskName: String) : Action
object CancelTask : Action

object LoadLargerScope : Action
object LoadSmallerScope : Action

object AddRandomOverdueTask : Action

data class UpdateSelectedScope(val scope: Scope?, val scopeSelectionInfo: ScopeSelectionInfo?) :
    Action

data class UpdateScopeSelectionInfo(val scopeSelectionInfo: ScopeSelectionInfo?) : Action
object ClearCreateTaskState : Action

// probably could move to global commits
data class UpdateTypedTaskList(
    val scopeType: ScopeType,
    val taskList: Flow<PagingData<TaskView>>
) : Action

data class UpdateExpandedTask(val task: Task) : Action

data class UpdateTaskList(val taskList: Flow<PagingData<TaskView>>) : Action
