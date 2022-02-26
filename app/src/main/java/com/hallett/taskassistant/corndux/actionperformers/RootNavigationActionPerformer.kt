package com.hallett.taskassistant.corndux.actionperformers

import androidx.paging.PagingConfig
import com.hallett.database.ITaskRepository
import com.hallett.scopes.scope_generator.IScopeGenerator
import com.hallett.taskassistant.corndux.BottomNavigationClicked
import com.hallett.taskassistant.corndux.FabClicked
import com.hallett.taskassistant.corndux.IActionPerformer
import com.hallett.taskassistant.corndux.NavigateSingleTop
import com.hallett.taskassistant.corndux.NavigateToRootDestination
import com.hallett.taskassistant.corndux.OverdueTasksClicked
import com.hallett.taskassistant.corndux.TaskAssistantAction
import com.hallett.taskassistant.corndux.TaskAssistantSideEffect
import com.hallett.taskassistant.corndux.TaskAssistantState
import com.hallett.taskassistant.corndux.TaskListClicked
import java.time.LocalDate

class RootNavigationActionPerformer(
    private val taskRepository: ITaskRepository,
    private val scopeGenerator: IScopeGenerator,
): IActionPerformer {
    override suspend fun performAction(
        action: TaskAssistantAction,
        state: TaskAssistantState,
        dispatchNewAction: (TaskAssistantAction) -> Unit,
        dispatchSideEffect: (TaskAssistantSideEffect) -> Unit
    ): TaskAssistantState = when(action) {
        is BottomNavigationClicked -> {
            dispatchSideEffect(NavigateToRootDestination(action.destination))
            when(action) {
                is TaskListClicked -> {
                    val defaultScope = scopeGenerator.generateScope()
                    val taskList = taskRepository.observeTasksForScope(PagingConfig(pageSize = 20), defaultScope)
                    state.copy(scope = defaultScope, tasks = taskList, screen = action.destination)
                }
                is OverdueTasksClicked -> {
                    val taskList = taskRepository.getOverdueTasks(PagingConfig(pageSize = 20), LocalDate.now())
                    state.copy(tasks = taskList, screen = action.destination)
                }
            }
        }
        is FabClicked -> {
            dispatchSideEffect(NavigateSingleTop(action.destination))
            state.copy(scope = null, screen = action.destination)
        }
        else -> state
    }
}