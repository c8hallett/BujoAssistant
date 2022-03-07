package com.hallett.taskassistant.corndux.actionperformers

import androidx.paging.PagingConfig
import com.hallett.corndux.Action
import com.hallett.corndux.SideEffect
import com.hallett.database.ITaskRepository
import com.hallett.scopes.model.ScopeType
import com.hallett.taskassistant.corndux.actions.AddRandomOverdueTask
import com.hallett.taskassistant.corndux.IActionPerformer
import com.hallett.taskassistant.corndux.OverdueTasksState
import com.hallett.taskassistant.corndux.actions.PerformInitialSetup
import com.hallett.taskassistant.corndux.TaskAssistantState
import com.hallett.taskassistant.ui.navigation.TaskNavDestination
import java.time.LocalDate

class OverdueTaskReducer(private val taskRepository: ITaskRepository): IActionPerformer {

    private val pagingConfig = PagingConfig(pageSize = 20)

    override suspend fun performAction(
        action: Action,
        state: TaskAssistantState,
        dispatchNewAction: (Action) -> Unit,
        dispatchSideEffect: (SideEffect) -> Unit
    ): TaskAssistantState {
        if(state.session.screen !is TaskNavDestination.OverdueTasks) return state
        return when(action) {
            is PerformInitialSetup -> {
                val taskList = taskRepository.getOverdueTasks(pagingConfig, LocalDate.now())
                state.updateOverdueTask { copy(taskList = taskList) }
            }
            is AddRandomOverdueTask -> {
                taskRepository.randomTask(ScopeType.values().random(), overdue = true)
                state
            }
            else -> state
        }
    }

    private inline fun TaskAssistantState.updateOverdueTask(update: OverdueTasksState.() -> OverdueTasksState): TaskAssistantState {
        return updateComponents { copy(overdueTask = overdueTask.update()) }
    }
}