package com.hallett.taskassistant.corndux

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.hallett.corndux.ActionPerformer
import com.hallett.database.ITaskRepository
import com.hallett.domain.coroutines.DispatchersWrapper
import com.hallett.scopes.model.Scope
import com.hallett.scopes.model.ScopeType
import com.hallett.taskassistant.di.PagerParams
import kotlinx.coroutines.flow.flowOn

class TaskAssistantActionPerformer(
    private val taskRepository: ITaskRepository,
    private val generatePager: (PagerParams) -> Pager<Scope, Scope>,
    private val dispatchers: DispatchersWrapper
): ActionPerformer<TaskAssistantState, TaskAssistantAction, TaskAssistantSideEffect>  {
    override suspend fun performAction(
        action: TaskAssistantAction,
        state: TaskAssistantState,
        dispatchNewAction: (TaskAssistantAction) -> Unit,
        dispatchSideEffect: (TaskAssistantSideEffect) -> Unit
    ): TaskAssistantState = when(action) {
        is UpdateTaskName -> state.copy(taskName = action.newTaskName)
        is UpdateTaskScope -> state.copy(scope = action.newTaskScope, scopeSelectionInfo = null)
        is SelectNewScopeType -> state.copy(scopeSelectionInfo = createScopeSelectionInfo(action.scopeType))
        is EnterScopeSelection -> state.copy(scopeSelectionInfo = createScopeSelectionInfo(ScopeType.DAY))
        is ScopeSelectionCancelled -> state.copy(scopeSelectionInfo = null)
        is SubmitTask -> {
            taskRepository.createNewTask(state.taskName, state.scope)
            dispatchSideEffect(NavigateUp)
            state
        }
        is CancelTask -> {
            dispatchSideEffect(NavigateUp)
            state
        }
    }

    private fun createScopeSelectionInfo(scopeType: ScopeType): ScopeSelectionInfo {
        val pagingConfig = PagingConfig(pageSize = 20)
        val scopes = generatePager(PagerParams(pagingConfig, scopeType))
            .flow
            .flowOn(dispatchers.default)

        return ScopeSelectionInfo(scopeType, scopes)
    }
}