package com.hallett.bujoass.database

import androidx.room.*

interface BujoTaskDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(task: BujoTaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(task: List<BujoTaskEntity>): Long

    @Update
    fun update(task: BujoTaskEntity)

    @Transaction
    fun upsert(task: BujoTaskEntity){
        when(val insert = insert(task)){
            -1L -> update(task) // conflict--update task instead
            else -> {} // no update
        }
    }

    @Delete
    fun delete(task: BujoTaskEntity)
}