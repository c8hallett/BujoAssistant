package com.hallett.bujoass.database

import androidx.room.*

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
}