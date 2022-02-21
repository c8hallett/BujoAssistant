package com.hallett.taskassistant.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.hallett.scopes.model.Scope
import com.hallett.scopes.scope_generator.IScopeGenerator
import com.hallett.database.TaskDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class TaskListViewModel(
    scopeGenerator: IScopeGenerator,
    private val taskDao: com.hallett.database.TaskDao,
) : ViewModel() {
    private val selectedScopeFlow: MutableStateFlow<Scope?> =
        MutableStateFlow(scopeGenerator.generateScope())

    fun observerCurrentScope(): Flow<Scope?> = selectedScopeFlow

    fun observeTasksForCurrentScope(): Flow<PagingData<com.hallett.domain.Task>> =
        selectedScopeFlow.flatMapLatest { currentScope ->
            Pager(PagingConfig(pageSize = 20)) { taskDao.getAllTaskForScopeInstancePage(currentScope) }
                .flow
                .flowOn(Dispatchers.Default)
                .map { pagedData ->
                    Log.i("ViewModel", "received new paged data for ${selectedScopeFlow.value}")
                    pagedData.map { savedTaskEntity ->
                        with(savedTaskEntity) {
                            com.hallett.domain.Task(
                                id = id,
                                taskName = taskName,
                                scope = scope,
                                status = status
                            )
                        }
                    }
                }
        }

    fun setCurrentScope(scope: Scope?) {
        viewModelScope.launch {
            selectedScopeFlow.emit(scope)
        }
    }
}