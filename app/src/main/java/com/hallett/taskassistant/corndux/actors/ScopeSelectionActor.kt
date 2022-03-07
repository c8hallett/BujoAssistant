package com.hallett.taskassistant.corndux.actors

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.hallett.corndux.Action
import com.hallett.corndux.SideEffect
import com.hallett.domain.coroutines.DispatchersWrapper
import com.hallett.scopes.model.Scope
import com.hallett.scopes.model.ScopeType
import com.hallett.taskassistant.corndux.CreateTaskState
import com.hallett.taskassistant.corndux.IActionPerformer
import com.hallett.taskassistant.corndux.IReducer
import com.hallett.taskassistant.corndux.ScopeSelectionInfo
import com.hallett.taskassistant.corndux.TaskAssistantState
import com.hallett.taskassistant.corndux.TasksListState
import com.hallett.taskassistant.corndux.actions.CancelScopeSelection
import com.hallett.taskassistant.corndux.actions.EnterScopeSelection
import com.hallett.taskassistant.corndux.actions.SelectNewScope
import com.hallett.taskassistant.corndux.actions.SelectNewScopeType
import com.hallett.taskassistant.di.PagerParams
import com.hallett.taskassistant.ui.navigation.TaskNavDestination
import kotlinx.coroutines.flow.flowOn

class ScopeSelectionActor(
    private val generatePager: (PagerParams) -> Pager<Scope, Scope>,
    private val dispatchers: DispatchersWrapper
): IActionPerformer, IReducer {

    private data class UpdateScopeSelectionInfo(
        val scopeSelectionInfo: ScopeSelectionInfo?
    ): Action

    override suspend fun performAction(
        state: TaskAssistantState,
        action: Action,
        dispatchNewAction: (Action) -> Unit,
    ) {
        when(action) {
            is SelectNewScopeType -> dispatchNewAction(
                UpdateScopeSelectionInfo(createScopeSelectionInfo(action.scopeType))
            )
            is EnterScopeSelection -> {
                val currentScopeType = when(state.session.screen) {
                    is TaskNavDestination.TaskList -> state.components.taskList.scope?.type
                    is TaskNavDestination.CreateTask -> state.components.createTask.scope?.type
                    else -> ScopeType.DAY
                } ?: ScopeType.DAY
                dispatchNewAction(
                    UpdateScopeSelectionInfo(createScopeSelectionInfo(currentScopeType))
                )
            }
            is CancelScopeSelection -> dispatchNewAction(
                UpdateScopeSelectionInfo(null)
            )
        }
    }

    private val pagingConfig = PagingConfig(pageSize = 20)
    private fun createScopeSelectionInfo(scopeType: ScopeType): ScopeSelectionInfo {
        val scopes = generatePager(PagerParams(pagingConfig, scopeType))
            .flow
            .flowOn(dispatchers.default)

        return ScopeSelectionInfo(scopeType, scopes)
    }

    override fun reduce(
        state: TaskAssistantState,
        action: Action,
        dispatchSideEffect: (SideEffect) -> Unit
    ): TaskAssistantState {
        return when(state.session.screen) {
            is TaskNavDestination.TaskList -> when(action) {
                is SelectNewScope -> state.updateTaskList { copy(scope = action.newTaskScope, scopeSelectionInfo = null) }
                is UpdateScopeSelectionInfo -> state.updateTaskList { copy(scopeSelectionInfo = action.scopeSelectionInfo) }
                else -> state
            }
            is TaskNavDestination.CreateTask -> when(action) {
                is SelectNewScope -> state.updateCreateTask { copy(scope = action.newTaskScope, scopeSelectionInfo = null) }
                is UpdateScopeSelectionInfo -> state.updateCreateTask { copy(scopeSelectionInfo = action.scopeSelectionInfo) }
                else -> state
            }
            else -> state
        }
    }

    private inline fun TaskAssistantState.updateCreateTask(update: CreateTaskState.() -> CreateTaskState): TaskAssistantState {
        return updateComponents { copy(createTask = createTask.update()) }
    }

    private inline fun TaskAssistantState.updateTaskList(update: TasksListState.() -> TasksListState): TaskAssistantState {
        return updateComponents { copy(taskList = taskList.update() ) }
    }
}