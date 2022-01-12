package com.hallett.taskassistant.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.hallett.scopes.IScopeGenerator
import com.hallett.scopes.Scope
import com.hallett.scopes.ScopeType
import com.hallett.taskassistant.ui.formatters.IFormatter
import com.hallett.taskassistant.ui.model.SelectableScope
import com.hallett.taskassistant.ui.paging.ScopePagingSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

class TaskAssistantViewModel(
    private val scopeGenerator: IScopeGenerator,
    private val scopeFormatter: IFormatter<Scope, SelectableScope>
): ViewModel() {

    private val scopeTypeSelected = MutableStateFlow(ScopeType.DAY)

    fun onNewScopeTypeSelected(scopeType: ScopeType) {
        scopeTypeSelected.value = scopeType
    }

    @FlowPreview
    fun observeScopes(): Flow<PagingData<SelectableScope>> = scopeTypeSelected.flatMapLatest { scopeType ->
        Log.i("TaskAssistantViewModel", "new scope type selected: $scopeType")
        Pager(
            config = PagingConfig(pageSize = 10),
            initialKey = scopeGenerator.generateScope(scopeType)
        ){ ScopePagingSource(scopeGenerator) }.flow.flowOn(Dispatchers.Default)
    }.map { pagingData -> pagingData.map { scopeFormatter.format(it) } }
}