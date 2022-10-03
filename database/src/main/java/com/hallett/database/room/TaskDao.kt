package com.hallett.database.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import com.hallett.database.room.TaskEntity.Companion.ID
import com.hallett.database.room.TaskEntity.Companion.TABLE_NAME
import com.hallett.database.room.TaskEntity.Companion.TASK_NAME
import com.hallett.database.room.TaskEntity.Companion.TASK_SCOPE_END_VALUE
import com.hallett.database.room.TaskEntity.Companion.TASK_SCOPE_TYPE
import com.hallett.database.room.TaskEntity.Companion.TASK_SCOPE_VALUE
import com.hallett.database.room.TaskEntity.Companion.TASK_STATUS
import com.hallett.domain.model.TaskStatus

@Dao
internal interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(task: TaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(task: List<TaskEntity>): List<Long>

    @Update
    suspend fun update(task: TaskEntity)

    @Transaction
    suspend fun upsert(task: TaskEntity) {
        when (insert(task)) {
            -1L -> update(task) // conflict--update task instead
            else -> {} // no conflict
        }
    }

    @Delete
    suspend fun delete(task: TaskEntity)

    @Query("DELETE FROM $TABLE_NAME WHERE $ID = :taskId")
    suspend fun delete(taskId: Long)

    @Query("DELETE FROM $TABLE_NAME")
    suspend fun clearTable()

    @Query("SELECT * FROM $TABLE_NAME WHERE $ID = :taskId")
    suspend fun getTask(taskId: Long): TaskEntity?

    @RawQuery(observedEntities = [TaskEntity::class])
    fun rawTasksQuery(query: SupportSQLiteQuery): PagingSource<Int, TaskEntity>

    @Query("SELECT * FROM $TABLE_NAME WHERE $TASK_SCOPE_END_VALUE < :value AND $TASK_STATUS IS NOT :excludeStatus")
    fun getAllOverdueTasks(
        value: Long,
        excludeStatus: TaskStatus = TaskStatus.COMPLETE
    ): PagingSource<Int, TaskEntity>

    @Query("SELECT * FROM $TABLE_NAME WHERE $TASK_SCOPE_VALUE > :value AND $TASK_NAME LIKE :filter AND $TASK_STATUS IS NOT :excludeStatus ORDER BY $TASK_SCOPE_VALUE ASC, $TASK_SCOPE_TYPE DESC")
    fun filterFutureTasks(
        value: Long,
        filter: String,
        excludeStatus: TaskStatus = TaskStatus.COMPLETE
    ): PagingSource<Int, TaskEntity>

    @Update(entity = TaskEntity::class)
    suspend fun updateTaskStatus(update: TaskEntity.StatusUpdate)

    @Update(entity = TaskEntity::class)
    suspend fun rescheduleTask(update: TaskEntity.ScopeUpdate)
}