package com.hallett.bujoass.database

import androidx.room.*
import com.hallett.bujoass.database.BujoTaskEntity.Companion.ID
import com.hallett.bujoass.database.BujoTaskEntity.Companion.SCOPE_VALUE
import com.hallett.bujoass.database.BujoTaskEntity.Companion.TABLE_NAME
import com.hallett.bujoass.database.BujoTaskEntity.Companion.TASK_SCOPE
import com.hallett.bujoass.domain.model.DScope
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface BujoTaskDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(task: BujoTaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(task: List<BujoTaskEntity>): List<Long>

    @Update
    fun update(task: BujoTaskEntity)

    @Transaction
    fun upsert(task: BujoTaskEntity){
        when(insert(task)){
            -1L -> update(task) // conflict--update task instead
            else -> {} // no conflict
        }
    }

    @Delete
    fun delete(task: BujoTaskEntity)

    @Query("DELETE FROM $TABLE_NAME WHERE $ID = :taskId")
    fun delete(taskId: Long)

    @Query("SELECT * FROM $TABLE_NAME WHERE $TASK_SCOPE IS :scope AND $SCOPE_VALUE IS :date")
    fun getAllTaskForScopeInstance(scope: DScope?, date: Date?): Flow<List<BujoTaskEntity>>

    @Query("SELECT * FROM $TABLE_NAME WHERE $ID = :taskId")
    fun observeTask(taskId: Long): Flow<BujoTaskEntity>
}