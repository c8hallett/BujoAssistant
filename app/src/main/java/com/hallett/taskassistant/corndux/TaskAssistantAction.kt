package com.hallett.taskassistant.corndux

import com.hallett.corndux.IAction
import com.hallett.scopes.model.Scope
import com.hallett.scopes.model.ScopeType
import com.hallett.taskassistant.ui.navigation.TaskNavDestination

sealed interface TaskAssistantAction: IAction
data class UpdateTaskScope(val newTaskScope: Scope?): TaskAssistantAction
data class SelectNewScopeType(val scopeType: ScopeType): TaskAssistantAction
object ScopeSelectionCancelled: TaskAssistantAction
object EnterScopeSelection: TaskAssistantAction
class SubmitTask(val taskName: String): TaskAssistantAction
object CancelTask: TaskAssistantAction

sealed class NavigationClicked(val destination: TaskNavDestination): TaskAssistantAction
class FabClicked(destination: TaskNavDestination): NavigationClicked(destination)

sealed class BottomNavigationClicked(destination: TaskNavDestination): NavigationClicked(destination)
class TaskListClicked(destination: TaskNavDestination): BottomNavigationClicked(destination)
class OverdueTasksClicked(destination: TaskNavDestination): BottomNavigationClicked(destination)