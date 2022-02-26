package com.hallett.taskassistant.corndux

import androidx.paging.PagingData
import com.hallett.corndux.IState
import com.hallett.scopes.model.Scope
import com.hallett.scopes.model.ScopeType
import kotlinx.coroutines.flow.Flow

data class TaskAssistantState(
    val scope: Scope?,
    val scopeSelectionInfo: ScopeSelectionInfo?
): IState

data class ScopeSelectionInfo(
    val scopeType: ScopeType,
    val scopes: Flow<PagingData<Scope>>
)
