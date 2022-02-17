package com.hallett.taskassistant.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hallett.taskassistant.database.converter.DateToLongConverter
import com.hallett.taskassistant.database.converter.ScopeToStringConverter
import com.hallett.taskassistant.database.task.TaskDao
import com.hallett.taskassistant.database.task.TaskEntity

@Database(
    entities = [TaskEntity::class],
    version = 1,
    exportSchema = false, // TODO: one day, export schema
)
@TypeConverters(
    DateToLongConverter::class,
    ScopeToStringConverter::class
)
abstract class BujoAssDatabase : RoomDatabase() {
    abstract fun bujoTaskDao(): TaskDao
}