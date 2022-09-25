package com.hallett.database

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.hallett.database.room.TaskDao
import com.hallett.database.room.TaskEntity
import com.hallett.domain.coroutines.DispatchersWrapper
import com.hallett.domain.model.Task
import com.hallett.domain.model.TaskStatus
import com.hallett.scopes.model.Scope
import com.hallett.scopes.model.ScopeType
import com.hallett.scopes.scope_generator.IScopeCalculator
import java.time.LocalDate
import java.util.Date
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

internal class TaskRepository(
    private val taskDao: TaskDao,
    private val dispatchers: DispatchersWrapper,
    private val scopeCalculator: IScopeCalculator
) : ITaskRepository {

    override fun getOverdueTasks(
        pagingConfig: PagingConfig,
        cutoff: LocalDate
    ): Flow<PagingData<Task>> =
        Pager(pagingConfig) { taskDao.getAllOverdueTasks(cutoff.toEpochDay()) }
            .flow
            .flowOn(dispatchers.io)
            .map { data -> data.map { entity -> entity.toTask() } }

    override fun observeFutureTasks(
        pagingConfig: PagingConfig,
        cutoff: LocalDate,
        search: String?,
    ): Flow<PagingData<Task>> = Pager(pagingConfig) {
        val searchFilter = if(search == null) "%" else "%$search%"
        taskDao.filterFutureTasks(cutoff.toEpochDay(), searchFilter)
    }
        .flow
        .flowOn(dispatchers.io)
        .map { data -> data.map { entity -> entity.toTask() } }

    override fun observeTasksForScope(
        pagingConfig: PagingConfig,
        scope: Scope?,
        search: String?,
        includeCompleted: Boolean
    ): Flow<PagingData<Task>> = Pager(pagingConfig) {
        val excluded = if(includeCompleted) null else TaskStatus.COMPLETE
        val searchFilter = if(search == null) "%" else "%$search%"
        taskDao.filterTasksForScope(scope?.type, scope?.value, searchFilter, excluded)
    }
        .flow
        .flowOn(dispatchers.io)
        .map { data -> data.map { entity -> entity.toTask() } }


    override suspend fun upsert(task: Task) {
        taskDao.upsert(task.toEntity())
    }

    override suspend fun getTask(taskId: Long): Task? =
        when (val entity = taskDao.getTask(taskId)) {
            null -> null
            else -> entity.toTask()
        }

    override suspend fun updateStatus(task: Task, status: TaskStatus) {
        val statusUpdate = TaskEntity.StatusUpdate(task.id, status)
        taskDao.updateTaskStatus(statusUpdate)
    }

    override suspend fun moveToNewScope(task: Task, scope: Scope?) {
        val scopeUpdate = TaskEntity.ScopeUpdate(task.id, scope.toEntity())
        taskDao.rescheduleTask(scopeUpdate)
    }

    override suspend fun randomTask(scopeType: ScopeType, overdue: Boolean) {
        val date = when {
            overdue -> LocalDate.now().minusYears(1L)
            else -> LocalDate.now()
        }
        val newTask = TaskEntity(
            taskName = "Auto-generated task #${System.currentTimeMillis() % 100_000}",
            scope = scopeCalculator.generateScope(scopeType, date).toEntity()
        )
        taskDao.insert(newTask)
    }

    private fun Task.toEntity(): TaskEntity = TaskEntity(
        id = id,
        taskName = name,
        status = status,
        scope = scope.toEntity(),
        updatedAt = Date()
    )

    private fun TaskEntity.toTask(): Task = Task(
        id = id,
        name = taskName,
        status = status,
        scope = scope.toScope()
    )

    private fun Scope?.toEntity(): TaskEntity.ScopeEntity? = this?.let {
        TaskEntity.ScopeEntity(
            type = type,
            value = value,
            endValue = scopeCalculator.getEndOfScope(it)
        )
    }

    override suspend fun deleteTask(task: Task) {
        taskDao.delete(task.id)
    }

    private fun TaskEntity.ScopeEntity?.toScope(): Scope? = this?.let {
        scopeCalculator.generateScope(it.type, it.value)
    }
}