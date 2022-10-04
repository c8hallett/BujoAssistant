package com.hallett.taskassistant.features.scopeSelection.corndux

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.hallett.corndux.Action
import com.hallett.corndux.Reducer
import com.hallett.corndux.SideEffect
import com.hallett.corndux.StatefulPerformer
import com.hallett.domain.coroutines.DispatchersWrapper
import com.hallett.scopes.model.Scope
import com.hallett.scopes.model.ScopeType
import com.hallett.taskassistant.main.corndux.UpdateSelectedScope
import com.hallett.taskassistant.main.di.PagerParams
import com.hallett.taskassistant.features.scopeSelection.CancelScopeSelection
import com.hallett.taskassistant.features.scopeSelection.ClickNewScope
import com.hallett.taskassistant.features.scopeSelection.ClickNewScopeType
import com.hallett.taskassistant.features.scopeSelection.EnterScopeSelection
import com.hallett.taskassistant.features.scopeSelection.UpdateScopeSelectionInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class ScopeSelectionActor(
    private val generatePager: (PagerParams) -> Pager<Scope, Scope>,
    private val dispatchers: DispatchersWrapper,
): StatefulPerformer<ScopeSelectionState>, Reducer<ScopeSelectionState> {

    private val pagingConfig = PagingConfig(pageSize = 20)

    override fun performAction(
        state: ScopeSelectionState,
        action: Action,
        dispatchAction: (Action) -> Unit,
        dispatchSideEffect: (SideEffect) -> Unit
    ) {
        when(action) {
            is EnterScopeSelection -> {
                val initialInfoAction = when(action.initialScope) {
                    null -> UpdateScopeSelectionInfo(
                        scopeType = ScopeType.DAY,
                        scopes = generateScopes(ScopeType.DAY)
                    )
                    else -> UpdateScopeSelectionInfo(
                        scopeType = action.initialScope.type,
                        scopes = generateScopes(action.initialScope.type)
                    )
                }
                dispatchAction(initialInfoAction)
            }
            is ClickNewScopeType -> dispatchAction(
                UpdateScopeSelectionInfo(
                    scopeType = action.scopeType,
                    scopes = generateScopes(action.scopeType)
                )
            )
            is ClickNewScope -> dispatchAction(UpdateSelectedScope(action.newTaskScope))
        }
    }

    override fun reduce(state: ScopeSelectionState, action: Action): ScopeSelectionState {
        return when(action){
            is UpdateScopeSelectionInfo -> state.copy(
                isEditing = true,
                scopeType = action.scopeType,
                scopes = action.scopes
            )
            is CancelScopeSelection -> state.copy(
                isEditing = false
            )
            is ClickNewScope -> state.copy(
                currentScope = action.newTaskScope,
                isEditing = false
            )
            else -> state
        }
    }

    private fun generateScopes(scopeType: ScopeType): Flow<PagingData<Scope>> {
        return generatePager(PagerParams(pagingConfig, scopeType))
            .flow
            .flowOn(dispatchers.default)

    }
}