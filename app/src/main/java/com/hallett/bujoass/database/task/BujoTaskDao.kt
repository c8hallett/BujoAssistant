package com.hallett.bujoass.database.task

import androidx.room.*
import com.hallett.bujoass.database.task.BujoTaskEntity.Companion.ID
import com.hallett.bujoass.database.task.BujoTaskEntity.Companion.TABLE_NAME
import com.hallett.bujoass.database.task.BujoTaskEntity.Companion.TASK_SCOPE
import kotlinx.coroutines.flow.Flow

@Dao
interface BujoTaskDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(task: BujoTaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(task: List<BujoTaskEntity>): List<Long>

    @Update
    suspend fun update(task: BujoTaskEntity)

    @Transaction
    suspend fun upsert(task: BujoTaskEntity){
        when(insert(task)){
            -1L -> update(task) // conflict--update task instead
            else -> {} // no conflict
        }
    }

    @Delete
    suspend fun delete(task: BujoTaskEntity)

    @Query("DELETE FROM $TABLE_NAME WHERE $ID = :taskId")
    suspend fun delete(taskId: Long)

    @Query("DELETE FROM $TABLE_NAME")
    suspend fun clearTable()

    @Query("SELECT * FROM $TABLE_NAME WHERE $TASK_SCOPE IS :scope")
    fun getAllTaskForScopeInstance(scope: Scope?): Flow<List<BujoTaskEntity>>

    @Query("SELECT * FROM $TABLE_NAME WHERE $ID = :taskId")
    fun observeTask(taskId: Long): Flow<BujoTaskEntity?>

    @Query("SELECT * FROM $TABLE_NAME WHERE $ID = :taskId")
    suspend fun getTask(taskId: Long): BujoTaskEntity?

    @Update(entity = BujoTaskEntity::class)
    suspend fun updateTaskStatus(update: BujoTaskEntity.StatusUpdate)

    @Update(entity = BujoTaskEntity::class)
    suspend fun rescheduleTask(update: BujoTaskEntity.NewScopeUpdate)
}