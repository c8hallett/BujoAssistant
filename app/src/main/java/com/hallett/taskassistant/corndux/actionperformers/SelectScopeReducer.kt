package com.hallett.taskassistant.corndux.actionperformers

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.hallett.corndux.Action
import com.hallett.corndux.SideEffect
import com.hallett.domain.coroutines.DispatchersWrapper
import com.hallett.scopes.model.Scope
import com.hallett.scopes.model.ScopeType
import com.hallett.taskassistant.corndux.actions.CancelScopeSelection
import com.hallett.taskassistant.corndux.CreateTaskState
import com.hallett.taskassistant.corndux.actions.EnterScopeSelection
import com.hallett.taskassistant.corndux.IActionPerformer
import com.hallett.taskassistant.corndux.ScopeSelectionInfo
import com.hallett.taskassistant.corndux.actions.SelectNewScope
import com.hallett.taskassistant.corndux.actions.SelectNewScopeType
import com.hallett.taskassistant.corndux.TaskAssistantState
import com.hallett.taskassistant.corndux.TasksListState
import com.hallett.taskassistant.di.PagerParams
import com.hallett.taskassistant.ui.navigation.TaskNavDestination
import kotlinx.coroutines.flow.flowOn

class SelectScopeReducer(
    private val generatePager: (PagerParams) -> Pager<Scope, Scope>,
    private val dispatchers: DispatchersWrapper
): IActionPerformer {

    override suspend fun performAction(
        action: Action,
        state: TaskAssistantState,
        dispatchNewAction: (Action) -> Unit,
        dispatchSideEffect: (SideEffect) -> Unit
    ): TaskAssistantState {
        return when(state.session.screen) {
            is TaskNavDestination.TaskList -> when(action) {
                is SelectNewScope -> state.updateTaskList { copy(scope = action.newTaskScope, scopeSelectionInfo = null) }
                is SelectNewScopeType -> state.updateTaskList { copy(scopeSelectionInfo = createScopeSelectionInfo(action.scopeType)) }
                is EnterScopeSelection -> state.updateTaskList { copy(scopeSelectionInfo = createScopeSelectionInfo(scope?.type ?: ScopeType.DAY)) }
                is CancelScopeSelection -> state.updateTaskList { copy(scopeSelectionInfo = null) }
                else -> state
            }
            is TaskNavDestination.CreateTask -> when(action) {
                is SelectNewScope -> state.updateCreateTask { copy(scope = action.newTaskScope, scopeSelectionInfo = null) }
                is SelectNewScopeType -> state.updateCreateTask { copy(scopeSelectionInfo = createScopeSelectionInfo(action.scopeType)) }
                is EnterScopeSelection -> state.updateCreateTask { copy(scopeSelectionInfo = createScopeSelectionInfo(scope?.type ?: ScopeType.DAY)) }
                is CancelScopeSelection -> state.updateCreateTask { copy(scopeSelectionInfo = null) }
                else -> state
            }
            else -> state
        }
    }

    private val pagingConfig = PagingConfig(pageSize = 20)
    private fun createScopeSelectionInfo(scopeType: ScopeType): ScopeSelectionInfo {
        val scopes = generatePager(PagerParams(pagingConfig, scopeType))
            .flow
            .flowOn(dispatchers.default)

        return ScopeSelectionInfo(scopeType, scopes)
    }

    private inline fun TaskAssistantState.updateCreateTask(update: CreateTaskState.() -> CreateTaskState): TaskAssistantState {
        return updateComponents { copy(createTask = createTask.update()) }
    }

    private inline fun TaskAssistantState.updateTaskList(update: TasksListState.() -> TasksListState): TaskAssistantState {
        return updateComponents { copy(taskList = taskList.update() ) }
    }

}