package com.hallett.taskassistant.corndux

import com.hallett.corndux.IAction
import com.hallett.domain.model.Task
import com.hallett.scopes.model.Scope
import com.hallett.scopes.model.ScopeType
import com.hallett.taskassistant.ui.navigation.TaskNavDestination

sealed interface TaskAssistantAction: IAction

object PerformInitialSetup: TaskAssistantAction

data class SelectNewScope(val newTaskScope: Scope?): TaskAssistantAction
data class SelectNewScopeType(val scopeType: ScopeType): TaskAssistantAction
object CancelScopeSelection: TaskAssistantAction
object EnterScopeSelection: TaskAssistantAction

class SubmitTask(val taskName: String): TaskAssistantAction
object CancelTask: TaskAssistantAction

sealed class NavigationClicked(val destination: TaskNavDestination): TaskAssistantAction
class FabClicked(destination: TaskNavDestination): NavigationClicked(destination)

sealed class BottomNavigationClicked(destination: TaskNavDestination): NavigationClicked(destination)
class TaskListClicked(destination: TaskNavDestination): BottomNavigationClicked(destination)
class DashboardClicked(destination: TaskNavDestination): BottomNavigationClicked(destination)
class OverdueTasksClicked(destination: TaskNavDestination): BottomNavigationClicked(destination)


object AddRandomOverdueTask: TaskAssistantAction

data class TaskClickedInList(val task: Task): TaskAssistantAction
data class DeleteTask(val task: Task): TaskAssistantAction
data class DeferTask(val task: Task): TaskAssistantAction
data class RescheduleTask(val task: Task): TaskAssistantAction
data class CompleteTask(val task: Task): TaskAssistantAction