package com.hallett.database

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.hallett.database.room.TaskQueryBuilder
import com.hallett.domain.model.Task
import com.hallett.domain.model.TaskStatus
import com.hallett.scopes.model.Scope
import com.hallett.scopes.model.ScopeType
import kotlinx.coroutines.flow.Flow

interface ITaskRepository {
    suspend fun upsert(task: Task)

    suspend fun updateStatus(task: Task, status: TaskStatus)

    suspend fun moveToNewScope(task: Task, scope: Scope?)

    suspend fun getTask(taskId: Long): Task?

    suspend fun randomTask(scopeType: ScopeType, overdue: Boolean)

    suspend fun deleteTask(task: Task)
    
    fun queryTasks(
        pagingConfig: PagingConfig,
        builder: TaskQueryBuilder
    ): Flow<PagingData<Task>>
}