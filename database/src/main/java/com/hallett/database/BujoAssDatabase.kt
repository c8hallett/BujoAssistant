package com.hallett.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hallett.database.converter.DateToLongConverter
import com.hallett.database.converter.ScopeToStringConverter

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