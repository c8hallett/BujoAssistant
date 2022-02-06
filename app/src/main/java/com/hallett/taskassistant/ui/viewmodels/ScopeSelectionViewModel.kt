package com.hallett.taskassistant.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.hallett.scopes.model.Scope
import com.hallett.scopes.model.ScopeType
import com.hallett.taskassistant.di.PagerParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class ScopeSelectionViewModel(
    private val generatePager: (PagerParams) -> Pager<Scope, Scope>
): ViewModel() {
    private companion object{
        val DEFAULT_SCOPE = ScopeType.DAY
    }

    private val scopeTypeSelected = MutableStateFlow(ScopeType.DAY)

    fun onNewScopeTypeSelected(scopeType: ScopeType) {
        viewModelScope.launch {
            scopeTypeSelected.value = scopeType
        }
    }

    fun observeScopeType(): Flow<ScopeType> = scopeTypeSelected

    fun observeScopeSelectorList(): Flow<PagingData<Scope>> = scopeTypeSelected.flatMapLatest { scopeType ->
        Log.i("TaskAssistantViewModel", "new scope type selected: $scopeType")
        generatePager(PagingConfig(pageSize = 10).toPagerParams(scopeType))
            .flow
            .flowOn(Dispatchers.Default)
    }

    private fun PagingConfig.toPagerParams(scopeType: ScopeType): PagerParams {
        return PagerParams(this, scopeType)
    }

}