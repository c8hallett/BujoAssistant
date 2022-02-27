package com.hallett.database.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.hallett.database.room.TaskEntity.Companion.ID
import com.hallett.database.room.TaskEntity.Companion.TABLE_NAME
import com.hallett.database.room.TaskEntity.Companion.TASK_SCOPE_TYPE
import com.hallett.database.room.TaskEntity.Companion.TASK_SCOPE_VALUE
import com.hallett.scopes.model.ScopeType
import java.time.LocalDate

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

    @Query("SELECT * FROM $TABLE_NAME WHERE $TASK_SCOPE_TYPE IS :scopeType AND $TASK_SCOPE_VALUE IS :value")
    fun getAllTaskForScope(scopeType: ScopeType?, value: LocalDate?): PagingSource<Int, TaskEntity>

    @Query("SELECT * FROM $TABLE_NAME WHERE $TASK_SCOPE_VALUE < :value")
    fun getAllOverdueTasks(value: LocalDate): PagingSource<Int, TaskEntity>

    @Update(entity = TaskEntity::class)
    suspend fun updateTaskStatus(update: TaskEntity.StatusUpdate)

    @Update(entity = TaskEntity::class)
    suspend fun rescheduleTask(update: TaskEntity.ScopeUpdate)
}