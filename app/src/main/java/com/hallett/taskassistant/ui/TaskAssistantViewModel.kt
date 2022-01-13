package com.hallett.taskassistant.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.hallett.scopes.model.Scope
import com.hallett.scopes.model.ScopeType
import com.hallett.taskassistant.ui.formatters.IFormatter
import com.hallett.taskassistant.ui.model.scope.SelectableScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.factory2


@ExperimentalCoroutinesApi
@FlowPreview
class TaskAssistantViewModel(
    private val scopeFormatter: IFormatter<Scope, SelectableScope>,
    override val kodein: Kodein
): ViewModel(), KodeinAware {
    private val generatePager: (PagingConfig, ScopeType) -> Pager<Scope, Scope> by kodein.factory2()

    private val scopeTypeSelected = MutableStateFlow(ScopeType.DAY)

    fun onNewScopeTypeSelected(scopeType: ScopeType) {
        scopeTypeSelected.value = scopeType
    }

    fun observeScopes(): Flow<PagingData<SelectableScope>> = scopeTypeSelected.flatMapLatest { scopeType ->
        Log.i("TaskAssistantViewModel", "new scope type selected: $scopeType")
        generatePager(PagingConfig(pageSize = 10), scopeType)
            .flow
            .flowOn(Dispatchers.Default)
    }.map { pagingData -> pagingData.map { scopeFormatter.format(it) } }
}