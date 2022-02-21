package com.hallett.taskassistant.ui.viewmodels

import android.util.Log
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
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class OverdueTaskViewModel(
    private val taskDao: TaskDao,
    private val scopeGenerator: IScopeGenerator
    ): ViewModel() {

    fun observeOverdueTasks(): Flow<PagingData<Task>> = Pager(PagingConfig(pageSize = 20)) {
        taskDao.getAllOverdueTasks(currentScope = scopeGenerator.generateScope())
    }.flow
        .flowOn(Dispatchers.Default)
        .map { pagedData ->
            pagedData.map { savedTaskEntity ->
                with(savedTaskEntity) {
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