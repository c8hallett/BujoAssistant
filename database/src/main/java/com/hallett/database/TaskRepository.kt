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
import com.hallett.scopes.scope_generator.IScopeGenerator
import java.time.LocalDate
import java.util.Date
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

internal class TaskRepository(
    private val taskDao: TaskDao,
    private val dispatchers: DispatchersWrapper,
    private val scopeGenerator: IScopeGenerator
): ITaskRepository {

    override fun getOverdueTasks(
        pagingConfig: PagingConfig,
        cutoff: LocalDate
    ): Flow<PagingData<Task>> = Pager(pagingConfig){ taskDao.getAllOverdueTasks(cutoff) }
        .flow
        .flowOn(dispatchers.io)
        .map { data -> data.map { entity -> entity.toTask() } }

    override fun observeTasksForScope(
        pagingConfig: PagingConfig,
        scope: Scope?
    ): Flow<PagingData<Task>>  = Pager(pagingConfig){
            taskDao.getAllTaskForScope(scope?.type, scope?.value)
        }
        .flow
        .flowOn(dispatchers.io)
        .map { data -> data.map { entity -> entity.toTask() } }


    override suspend fun upsert(task: Task) {
        taskDao.insert(task.toEntity())
    }

    override suspend fun createNewTask(taskName: String, scope: Scope?) {
        taskDao.insert(
            TaskEntity(
                taskName = taskName,
                scope = scope.toEntity()
            )
        )
    }

    override suspend fun getTask(taskId: Long): Task? = when(val entity = taskDao.getTask(taskId)) {
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
            scope = scopeGenerator.generateScope(scopeType, date).toEntity()
        )
        taskDao.insert(newTask)
    }

    private fun Task.toEntity(): TaskEntity = TaskEntity(
        id = id,
        taskName = taskName,
        status = status,
        scope = scope.toEntity(),
        updatedAt = Date()
    )

    private fun TaskEntity.toTask(): Task = Task(
        id = id,
        taskName = taskName,
        status = status,
        scope = scope.toScope()
    )

    private fun Scope?.toEntity(): TaskEntity.ScopeEntity? = this?.let {
        TaskEntity.ScopeEntity(
            type = type,
            value = value
        )
    }

    override suspend fun deleteTask(task: Task) {
        taskDao.delete(task.id)
    }

    private fun TaskEntity.ScopeEntity?.toScope(): Scope? = this?.let {
        scopeGenerator.generateScope(it.type, it.value)
    }
}