package com.hallett.taskassistant.ui.theme

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.hallett.scopes.scope_generator.IScopeGenerator
import com.hallett.taskassistant.database.task.TaskDao
import com.hallett.taskassistant.domain.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class TaskListViewModel(
    private val scopeGenerator: IScopeGenerator,
    private val taskDao: TaskDao,
): ViewModel() {
    private val selectedScopeFlow = MutableStateFlow(scopeGenerator.generateScope())
    fun observeTasksForCurrentScope(): Flow<PagingData<Task>> = selectedScopeFlow.flatMapLatest { currentScope ->
        Pager(PagingConfig(pageSize = 20)) { taskDao.getAllTaskForScopeInstancePage(currentScope) }
            .flow
            .flowOn(Dispatchers.Default)
            .map { pagedData ->
                pagedData.map { savedTaskEntity ->
                    with(savedTaskEntity){
                        Task(
                            id = id,
                            taskName = taskName,
                            scope = scope,
                            status = status
                        )
                    }
                }
            }
    }
}