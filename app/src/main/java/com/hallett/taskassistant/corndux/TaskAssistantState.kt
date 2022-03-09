package com.hallett.taskassistant.corndux

import androidx.paging.PagingData
import com.hallett.corndux.IState
import com.hallett.domain.model.Task
import com.hallett.scopes.model.Scope
import com.hallett.scopes.model.ScopeType
import com.hallett.taskassistant.ui.navigation.TaskNavDestination
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

data class TaskAssistantState(
    val session: Session = Session(),
    val components: Components = Components(),
): IState {
    inline fun updateSession(update: Session.() -> Session): TaskAssistantState {
        return copy( session = session.update() )
    }

    inline fun updateComponents(update: Components.() -> Components): TaskAssistantState {
        return copy( components = components.update() )
    }
}

data class Session(
    val screen: TaskNavDestination = TaskNavDestination.startDestination,
)

data class Components(
    val dashboard: DashboardState = DashboardState(),
    val createTask: CreateTaskState = CreateTaskState(),
    val overdueTask: OverdueTasksState = OverdueTasksState(),
    val taskList: TasksListState = TasksListState(),
    val futureTasks: FutureTaskListState = FutureTaskListState()
)


// TODO: each component might have an error state?
data class DashboardState(
    val taskList: Flow<PagingData<Task>> = flowOf(),
    val scopeType: ScopeType = ScopeType.DAY // eventually make this nullable?
)

data class FutureTaskListState(
    val unscheduledList: Flow<PagingData<Task>> = flowOf(),
    val scheduledList: Flow<PagingData<Task>> = flowOf(),
    val expandedList: ExpandedList = ExpandedList.UNSCHEDULED
) {
    enum class ExpandedList {
        SCHEDULED,
        UNSCHEDULED
    }
}

data class CreateTaskState(
    val taskName: String = "",
    val scope: Scope? = null,
    val scopeSelectionInfo: ScopeSelectionInfo? = null,
)

data class TasksListState(
    val currentlyExpandedTask: Task? = null,
    val taskList: Flow<PagingData<Task>> = flowOf(),
    val scope: Scope? = null,
    val scopeSelectionInfo: ScopeSelectionInfo? = null,
)

data class OverdueTasksState(
    val taskList: Flow<PagingData<Task>> = flowOf(),
)

data class ScopeSelectionInfo(
    val scopeType: ScopeType,
    val scopes: Flow<PagingData<Scope>>
)


sealed class Error(message: String) {
    object EmptyTaskName: Error("Task name cannot be blank.")
}
