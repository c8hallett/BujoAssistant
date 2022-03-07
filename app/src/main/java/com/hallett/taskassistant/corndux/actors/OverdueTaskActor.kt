package com.hallett.taskassistant.corndux.actors

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.hallett.corndux.Action
import com.hallett.corndux.SideEffect
import com.hallett.database.ITaskRepository
import com.hallett.domain.model.Task
import com.hallett.logging.logI
import com.hallett.scopes.model.ScopeType
import com.hallett.taskassistant.corndux.IActionPerformer
import com.hallett.taskassistant.corndux.IReducer
import com.hallett.taskassistant.corndux.OverdueTasksState
import com.hallett.taskassistant.corndux.TaskAssistantState
import com.hallett.taskassistant.corndux.actions.AddRandomOverdueTask
import com.hallett.taskassistant.corndux.actions.PerformInitialSetup
import com.hallett.taskassistant.ui.navigation.TaskNavDestination
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

class OverdueTaskActor(private val taskRepository: ITaskRepository): IActionPerformer, IReducer {

    private data class UpdateOverdueTaskList(
        val taskList: Flow<PagingData<Task>>
    ): Action

    private val pagingConfig = PagingConfig(pageSize = 20)

    override suspend fun performAction(
        state: TaskAssistantState,
        action: Action,
        dispatchNewAction: (Action) -> Unit
    ) {
        if(state.session.screen is TaskNavDestination.OverdueTasks) {
            when(action) {
                is PerformInitialSetup -> {
                    val newOverdueTaskList = taskRepository.getOverdueTasks(pagingConfig, LocalDate.now())
                    dispatchNewAction(
                        UpdateOverdueTaskList(taskList = newOverdueTaskList)
                    )
                }
                is AddRandomOverdueTask -> {
                    taskRepository.randomTask(ScopeType.values().random(), overdue = true)
                }
            }
        }
    }

    override fun reduce(
        state: TaskAssistantState,
        action: Action,
        dispatchSideEffect: (SideEffect) -> Unit
    ): TaskAssistantState = when(action) {
        is UpdateOverdueTaskList -> state.updateOverdueTask { copy(taskList = taskList) }.also { logI("Updated overdue task list") }
        else -> state
    }

    private inline fun TaskAssistantState.updateOverdueTask(update: OverdueTasksState.() -> OverdueTasksState): TaskAssistantState {
        return updateComponents { copy(overdueTask = overdueTask.update()) }
    }
}