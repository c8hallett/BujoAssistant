package com.hallett.taskassistant.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.hallett.database.ITaskRepository
import com.hallett.scopes.model.Scope
import com.hallett.domain.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class TaskListViewModel(
    private val taskRepo: ITaskRepository
) : ViewModel() {
    private val selectedScopeFlow: MutableStateFlow<Scope?> = MutableStateFlow(null)

    fun observerCurrentScope(): Flow<Scope?> = selectedScopeFlow

    fun observeTasksForCurrentScope(): Flow<PagingData<Task>> =
        selectedScopeFlow.flatMapLatest { currentScope ->
            taskRepo.observeTasksForScope(PagingConfig(pageSize = 20), currentScope)
        }

    fun setCurrentScope(scope: Scope?) {
        viewModelScope.launch {
            selectedScopeFlow.emit(scope)
        }
    }
}