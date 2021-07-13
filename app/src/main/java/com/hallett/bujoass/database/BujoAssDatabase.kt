package com.hallett.bujoass.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hallett.bujoass.database.converter.DateToLongConverter
import com.hallett.bujoass.database.converter.ScopeToStringConverter
import com.hallett.bujoass.database.task.BujoTaskDao
import com.hallett.bujoass.database.task.BujoTaskEntity

@Database(
    entities = [BujoTaskEntity::class],
    version = 1
)
@TypeConverters(
    DateToLongConverter::class,
    ScopeToStringConverter::class
)
abstract class BujoAssDatabase: RoomDatabase() {
    abstract fun bujoTaskDao(): BujoTaskDao
}