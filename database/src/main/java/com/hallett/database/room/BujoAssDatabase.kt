package com.hallett.database.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hallett.database.converter.DateToLongConverter
import com.hallett.database.converter.EnumToIntConverter
import com.hallett.database.converter.LocalDateToLongConverter

@Database(
    entities = [TaskEntity::class],
    version = 1,
    exportSchema = false, // TODO: one day, export schema
)
@TypeConverters(
    DateToLongConverter::class,
    LocalDateToLongConverter::class,
    EnumToIntConverter::class,
)
internal abstract class BujoAssDatabase : RoomDatabase() {
    abstract fun bujoTaskDao(): TaskDao
}