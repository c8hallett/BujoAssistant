package com.hallett.database

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.hallett.domain.model.Task
import com.hallett.domain.model.TaskStatus
import com.hallett.scopes.model.Scope
import com.hallett.scopes.model.ScopeType
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

interface ITaskRepository {
    fun getOverdueTasks(pagingConfig: PagingConfig, cutoff: LocalDate): Flow<PagingData<Task>>

    fun observeTasksForScope(pagingConfig: PagingConfig, scope: Scope?): Flow<PagingData<Task>>

    suspend fun upsert(task: Task)

    suspend fun createNewTask(taskName: String, scope: Scope?)

    suspend fun updateStatus(taskId: Long, status: TaskStatus)

    suspend fun moveToNewScope(taskId: Long, scope: Scope?)

    suspend fun getTask(taskId: Long): Task?

    suspend fun randomTask(scopeType: ScopeType, overdue: Boolean)

}