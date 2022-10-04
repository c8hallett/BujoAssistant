package com.hallett.taskassistant.features.scopeSelection.corndux

import androidx.paging.PagingData
import com.hallett.corndux.IState
import com.hallett.corndux.Store
import com.hallett.scopes.model.Scope
import com.hallett.scopes.model.ScopeType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class ScopeSelectionStore(
    scopeSelectionActor: ScopeSelectionActor,
    coroutineScope: CoroutineScope
): Store<ScopeSelectionState>(
    initialState = ScopeSelectionState(),
    actors = listOf(scopeSelectionActor),
    scope = coroutineScope
)

data class ScopeSelectionState(
    val isEditing: Boolean = false,
    val currentScope: Scope? = null,
    val scopeType: ScopeType = ScopeType.DAY,
    val scopes: Flow<PagingData<Scope>> = flowOf()
): IState