package com.hallett.taskassistant.ui.model

import androidx.paging.PagingData
import com.hallett.scopes.model.Scope
import com.hallett.scopes.model.ScopeType
import kotlinx.coroutines.flow.Flow

data class ScopeSelectionInfo(
    val scopeType: ScopeType,
    val scopes: Flow<PagingData<Scope>>
)