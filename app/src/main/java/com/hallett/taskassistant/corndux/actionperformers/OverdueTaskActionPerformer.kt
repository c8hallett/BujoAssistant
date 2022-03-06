package com.hallett.taskassistant.corndux.actionperformers

import androidx.paging.PagingConfig
import com.hallett.database.ITaskRepository
import com.hallett.scopes.model.ScopeType
import com.hallett.taskassistant.corndux.AddRandomOverdueTask
import com.hallett.taskassistant.corndux.IActionPerformer
import com.hallett.taskassistant.corndux.OverdueTasksState
import com.hallett.taskassistant.corndux.PerformInitialSetup
import com.hallett.taskassistant.corndux.TaskAssistantAction
import com.hallett.taskassistant.corndux.TaskAssistantSideEffect
import com.hallett.taskassistant.corndux.TaskAssistantState
import com.hallett.taskassistant.ui.navigation.TaskNavDestination
import java.time.LocalDate

class OverdueTaskActionPerformer(private val taskRepository: ITaskRepository): IActionPerformer {

    private val pagingConfig = PagingConfig(pageSize = 20)

    override suspend fun performAction(
        action: TaskAssistantAction,
        state: TaskAssistantState,
        dispatchNewAction: (TaskAssistantAction) -> Unit,
        dispatchSideEffect: (TaskAssistantSideEffect) -> Unit
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