package com.hallett.taskassistant.corndux.performers.actions

import com.hallett.corndux.Action
import com.hallett.domain.model.Task
import com.hallett.scopes.model.Scope
import com.hallett.scopes.model.ScopeType
import com.hallett.taskassistant.corndux.FutureTaskListState
import com.hallett.taskassistant.ui.navigation.TaskNavDestination

sealed interface CreateTaskAction: Action {
    data class SelectNewScope(val newTaskScope: Scope?) : CreateTaskAction
    data class SelectNewScopeType(val scopeType: ScopeType) : CreateTaskAction
    object CancelScopeSelection : CreateTaskAction
    object EnterScopeSelection : CreateTaskAction
}
class SubmitTask(val taskName: String) : CreateTaskAction
object CancelTask : CreateTaskAction

sealed interface OverdueTaskAction: Action {
    data class ClickTaskInList(val task: Task): OverdueTaskAction
}
object AddRandomOverdueTask : OverdueTaskAction

sealed interface DashboardAction: Action {
    data class ClickTaskInList(val task: Task): DashboardAction
}
object LoadLargerScope : DashboardAction
object LoadSmallerScope : DashboardAction

sealed interface FutureTaskAction: Action {
    data class ClickTaskInList(val task: Task): FutureTaskAction
}
data class ExpandList(val list: FutureTaskListState.ExpandedList) : FutureTaskAction

sealed interface TaskAction : Action
data class DeleteTask(val task: Task) : TaskAction
data class DeferTask(val task: Task) : TaskAction
data class RescheduleTask(val task: Task) : TaskAction
data class MarkTaskAsComplete(val task: Task) : TaskAction
data class MarkTaskAsIncomplete(val task: Task) : TaskAction

sealed interface TaskListAction: Action {
    data class ClickTaskInList(val task: Task): TaskListAction
    data class SelectNewScope(val newTaskScope: Scope?) : TaskListAction
    data class SelectNewScopeType(val scopeType: ScopeType) : TaskListAction
    object CancelScopeSelection : TaskListAction
    object EnterScopeSelection : TaskListAction
}
