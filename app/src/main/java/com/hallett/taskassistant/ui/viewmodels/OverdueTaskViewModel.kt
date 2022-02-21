package com.hallett.taskassistant.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.hallett.database.ITaskRepository
import com.hallett.scopes.model.ScopeType
import com.hallett.domain.model.Task
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class OverdueTaskViewModel(
    private val taskRepo: ITaskRepository
    ): ViewModel() {

    fun observeOverdueTasks(): Flow<PagingData<Task>> = taskRepo.getOverdueTasks(PagingConfig(pageSize = 20), LocalDate.now())

    fun addRandomOverdueTask() {
        viewModelScope.launch {
            taskRepo.randomTask(ScopeType.values().random(), true)
        }
    }
}