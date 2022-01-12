package com.hallett.taskassistant.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hallett.taskassistant.database.converter.DateToLongConverter
import com.hallett.taskassistant.database.converter.ScopeToStringConverter
import com.hallett.taskassistant.database.task.BujoTaskDao
import com.hallett.taskassistant.database.task.BujoTaskEntity

@Database(
    entities = [BujoTaskEntity::class],
    version = 1,
    exportSchema = false, // TODO: one day, export schema
)
@TypeConverters(
    DateToLongConverter::class,
    ScopeToStringConverter::class
)
abstract class BujoAssDatabase: RoomDatabase() {
    abstract fun bujoTaskDao(): BujoTaskDao
}