package com.hallett.taskassistant.database.task

import androidx.paging.PagingSource
import androidx.room.*
import com.hallett.taskassistant.database.task.TaskEntity.Companion.ID
import com.hallett.taskassistant.database.task.TaskEntity.Companion.TABLE_NAME
import com.hallett.taskassistant.database.task.TaskEntity.Companion.TASK_SCOPE
import com.hallett.scopes.model.Scope
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(task: TaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(task: List<TaskEntity>): List<Long>

    @Update
    suspend fun update(task: TaskEntity)

    @Transaction
    suspend fun upsert(task: TaskEntity){
        when(insert(task)){
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

    @Query("SELECT * FROM $TABLE_NAME WHERE $TASK_SCOPE IS :scope")
    fun getAllTaskForScopeInstance(scope: Scope?): Flow<List<TaskEntity>>

    @Query("SELECT * FROM $TABLE_NAME WHERE $TASK_SCOPE IS :scope")
    fun getAllTaskForScopeInstancePage(scope: Scope?): PagingSource<Int, TaskEntity>

    @Query("SELECT * FROM $TABLE_NAME WHERE $ID = :taskId")
    fun observeTask(taskId: Long): Flow<TaskEntity?>

    @Query("SELECT * FROM $TABLE_NAME WHERE $ID = :taskId")
    suspend fun getTask(taskId: Long): TaskEntity?

    @Update(entity = TaskEntity::class)
    suspend fun updateTaskStatus(update: TaskEntity.StatusUpdate)

    @Update(entity = TaskEntity::class)
    suspend fun rescheduleTask(update: TaskEntity.NewScopeUpdate)
}