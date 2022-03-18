package com.hallett.taskassistant.corndux

import com.hallett.corndux.Action
import com.hallett.domain.model.Task
import com.hallett.scopes.model.Scope
import com.hallett.scopes.model.ScopeType
import com.hallett.taskassistant.mainNavigation.TaskNavDestination

sealed class NavigationAction(val destination: TaskNavDestination) : Action
class FabClicked(destination: TaskNavDestination) : NavigationAction(destination)
class BottomNavigationClicked(destination: TaskNavDestination) : NavigationAction(destination)

data class SubmitTask(val taskName: String) : Action
object CancelTask : Action

object LoadLargerScope : Action
object LoadSmallerScope : Action

object AddRandomOverdueTask : Action

data class ClickTaskInList(val task: Task) : Action

sealed interface TaskAction : Action
data class DeleteTask(val task: Task) : TaskAction
data class DeferTask(val task: Task) : TaskAction
data class RescheduleTask(val task: Task) : TaskAction
data class MarkTaskAsComplete(val task: Task) : TaskAction
data class MarkTaskAsIncomplete(val task: Task) : TaskAction

sealed interface ScopeSelectionAction : Action
data class ClickNewScope(val newTaskScope: Scope?) : ScopeSelectionAction
data class ClickNewScopeType(val scopeType: ScopeType) : ScopeSelectionAction
object CancelScopeSelection : ScopeSelectionAction
object EnterScopeSelection : ScopeSelectionAction
