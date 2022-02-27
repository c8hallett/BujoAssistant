package com.hallett.taskassistant.corndux.actionperformers

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.hallett.domain.coroutines.DispatchersWrapper
import com.hallett.scopes.model.Scope
import com.hallett.scopes.model.ScopeType
import com.hallett.taskassistant.corndux.CancelScopeSelection
import com.hallett.taskassistant.corndux.EnterScopeSelection
import com.hallett.taskassistant.corndux.IActionPerformer
import com.hallett.taskassistant.corndux.ScopeSelectionInfo
import com.hallett.taskassistant.corndux.SelectNewScope
import com.hallett.taskassistant.corndux.SelectNewScopeType
import com.hallett.taskassistant.corndux.TaskAssistantAction
import com.hallett.taskassistant.corndux.TaskAssistantSideEffect
import com.hallett.taskassistant.corndux.TaskAssistantState
import com.hallett.taskassistant.di.PagerParams
import kotlinx.coroutines.flow.flowOn

class SelectScopeActionPerformer(
    private val generatePager: (PagerParams) -> Pager<Scope, Scope>,
    private val dispatchers: DispatchersWrapper
): IActionPerformer {

    override suspend fun performAction(
        action: TaskAssistantAction,
        state: TaskAssistantState,
        dispatchNewAction: (TaskAssistantAction) -> Unit,
        dispatchSideEffect: (TaskAssistantSideEffect) -> Unit
    ): TaskAssistantState {
        return when(action) {
            is SelectNewScope -> state.copy(scope = action.newTaskScope, scopeSelectionInfo = null)
            is SelectNewScopeType -> state.copy(scopeSelectionInfo = createScopeSelectionInfo(action.scopeType))
            is EnterScopeSelection -> state.copy(scopeSelectionInfo = createScopeSelectionInfo(state.scope?.type ?: ScopeType.DAY))
            is CancelScopeSelection -> state.copy(scopeSelectionInfo = null)
            else -> state
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